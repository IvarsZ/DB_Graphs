package experiments;

import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;

import java.sql.SQLException;
import java.util.ArrayList;

import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;
import tests.mySql.MySqlGraphTest;
import builders.PropertyIndexer;
import builders.TreeBuilder;

public class RandomTrees {
	
	public static void main(String[] args) throws SQLException {
		
		
	
		// Prepares the graph.
		IPersistentGraph<MySqlVertex, MySqlEdge> mySqlGraph = new MySqlGraph("rtt1", MySqlGraphTest.getConnector());
		mySqlGraph.clear();
		
		long size = 1000;
		int leafCount = 10;
		long seed = 958723051162497643L;
		ArrayList<String> numbersToIndex = new ArrayList<String>();
		numbersToIndex.add("300");
		numbersToIndex.add("600");
		PropertyIndexer indexer = new PropertyIndexer("number", numbersToIndex, mySqlGraph);
		TreeBuilder builder = new TreeBuilder(size, leafCount, seed, indexer);
		builder.build(mySqlGraph);
		
		MySqlVertex v1 = mySqlGraph.index().forVertices("number").getFirst("number", 300 + "");
		MySqlVertex v2 = mySqlGraph.index().forVertices("number").getFirst("number", 600 + "");
		
		long start = System.currentTimeMillis();
		mySqlGraph.getOperator().findLowestCommonAncestors(v1, v2, 1000, emptyList(), Direction.INCOMING);
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
	
	// TODO : move.
	private static ArrayList<String> emptyList() {
		return new ArrayList<String>();
	}
}
