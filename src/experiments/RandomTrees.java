package experiments;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;
import tests.mySql.MySqlGraphTest;
import builders.PropertyIndexer;
import builders.TreeBuilder;

public class RandomTrees {

	private static final String NUMBERS_INDEX = "numbers";

	TreeBuilder builder;

	private RandomTrees(long size, int spawnCount, long seed, List<String> numbersToIndex) {

		// Creates and indexer for the builder and then the builder itself.
		PropertyIndexer indexer = new PropertyIndexer(NUMBERS_INDEX, TreeBuilder.NUMBER_KEY, numbersToIndex);
		builder = new TreeBuilder(size, spawnCount);
	}

	private IPersistentGraph<MySqlVertex, MySqlEdge> buildMySql(String name, boolean rewrite) throws SQLException {

		// (Re)creates the mysql graph.
		IPersistentGraph<MySqlVertex, MySqlEdge> mySqlGraph = new MySqlGraph(name, MySqlGraphTest.getConnector());

		// If necessary writes the mysql graph.
		if (rewrite || builder.isWrittenTo(mySqlGraph) == false) {

			mySqlGraph.clear();
			builder.build(mySqlGraph);
		}

		return mySqlGraph;
	}

	private IPersistentGraph<Neo4jVertex, Neo4jEdge> buildNeo4j(String name, boolean rewrite) {

		// (Re)creates the neo4j graph.
		IPersistentGraph<Neo4jVertex, Neo4jEdge> neo4jGraph = new Neo4jGraph(name);

		// If necessary writes the neo4j graph.
		if (rewrite || builder.isWrittenTo(neo4jGraph) == false) {

			neo4jGraph.clear();
			builder.build(neo4jGraph);
		}

		return neo4jGraph;
	}

	private static <V extends IVertex, E extends IEdge> void execute(String v1Number, String v2Number, IPersistentGraph<V, E> graph) {

		V v1 = graph.index().forVertices("numbers").getFirst("number", v1Number);
		V v2 = graph.index().forVertices("numbers").getFirst("number", v2Number);

		long start = System.currentTimeMillis();

		graph.getOperator().findLowestCommonAncestors(v1, v2, 5000, emptyList(), Direction.INCOMING);

		long end = System.currentTimeMillis();
		
		long time = end - start;
		System.out.println("\tfind lowest common ancestors for " + v1Number + " and " + v2Number + " time is " + time);
	}

	public static void main(String[] args) throws SQLException {

		// Creates the experiment,
		RandomTrees experiment = new RandomTrees(10000, 10, 958723051162497643L, Arrays.asList("1000", "2000", "3000", "4000", "5000"));

		// runs it on mysql,
		System.out.println("mysql_rtt1");
		IPersistentGraph<MySqlVertex, MySqlEdge> mySqlGraph = experiment.buildMySql("mysql_rtt1", false);
		execute("3000", "5000", mySqlGraph);
		mySqlGraph.close();

		// and neo4j.
		System.out.println("neo4j_rtt1");
		IPersistentGraph<Neo4jVertex, Neo4jEdge> neo4jGraph = experiment.buildNeo4j("neo4j_rtt1", false);
		execute("3000", "5000", neo4jGraph);
		neo4jGraph.close();
	}

	// TODO : move.
	private static ArrayList<String> emptyList() {
		return new ArrayList<String>();
	}
}
