package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import converters.Indexers;

import util.MySqlUtil;
import util.Neo4jUtil;

import graphs.*;
import graphsInterfaces.IPropertyGraph;
import graphsInterfaces.IPropertyVertex;


public class TestGraphs {

	private static final String graph1Name = "graph1";
	private static final String graph1IndexName = graph1Name + "NodeNames";

	private static final String graph2Name = "graph2";
	private static final String graph2IndexName = graph2Name + "NodeNames";


	private static IPropertyGraph createSimpleGraph(int nodeCount, int[][] edges) {

		IPropertyGraph graph = new PropertyGraph();

		// Adds simple nodes and edges, where nodes have a name.
		for (int i = 0; i < nodeCount; i++) {
			IPropertyVertex vertex = new PropertyVertex();
			graph.addVertex(vertex);
		}
		for (int[] edge : edges) {
			graph.createEdge(graph.getVertex(edge[0]), graph.getVertex(edge[1]));
		}

		return graph;
	}

	/**
	 * 
	 * Writes graph1 to the neo4j unless it's already written.
	 * 
	 */
	protected static void writeGraph1ToNeo4j(GraphDatabaseService graphDb) {

		// Check if is not already written - a neo4j root with that name does not exist.
		if (Neo4jUtil.getNeo4jRoot(graphDb, graph1Name) == null) {

			// Creates the graph used in testing.
			IPropertyGraph graph1 = createTestGraph1();

			// Names all nodes.
			for (int i = 0; i < 9; i++) {
				graph1.getVertex(i).setProperty("name", "node" + i);
			}

			// Writes the graph to the database.
			graph1.writeToNeo4j(graphDb, graph1Name, Indexers.indexNodeProperty(graph1IndexName, "name"));
		}
	}

	protected static Node getGraph1Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph1IndexName).query("name", "node" + i).getSingle();
	}

	protected static void writeGraph1ToMySql(Connection connection, String tableName) throws SQLException {

		// Check if is not already written
		if (MySqlUtil.doesMySqlNameExists(connection, tableName) == false) {

			// Creates the graph used in testing.
			IPropertyGraph graph1 = createTestGraph1();
			
			// Names all nodes.
			for (int i = 0; i < 9; i++) {
				graph1.getVertex(i).setProperty("name", "node" + i);
			}

			// Writes the graph to the database.
			graph1.writeToMySql(connection, tableName);
		}
	}

	private static IPropertyGraph createTestGraph1() {

		int[][] edges = {{0,1}, {0,4}, {2,8}, {5,1}, {5,2}, {5,4}, {5,8}, {6,0}, {6,5}, {7,5}};
		return createSimpleGraph(9, edges);
	}

	/**
	 * 
	 * Large linked list.
	 * 
	 * @return
	 */
	protected static void writeGraph2ToNeo4j(GraphDatabaseService graphDb) {

		// Check if is not already written - a neo4j root with that name does not exist.
		if (Neo4jUtil.getNeo4jRoot(graphDb, graph2Name) == null) {

			// Creates the graph used in testing. 
			int count = 10000;
			int[][] edges = new int[count][2];

			// Adds edges (0,1), (1,2), (2,3), ..., (count - 2, count - 1).
			for (int i = 0; i < count - 1; i++) {
				edges[i][0] = i;
				edges[i][1] = i + 1;
			}

			IPropertyGraph graph2 = createSimpleGraph(count, edges);

			// Names the first 10 nodes.
			for (int i = 0; i < 10; i++) {
				graph2.getVertex(i).setProperty("name", "node" + i);
			}

			// Writes the graph to the database.
			graph2.writeToNeo4j(graphDb, graph2Name, Indexers.indexNodeProperty(graph2IndexName, "name"));
		}
	}

	protected static Node getGraph2Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph2IndexName).query("name", "node" + i).getSingle();
	}
}
