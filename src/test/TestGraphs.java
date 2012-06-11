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

	public static final String graph1Name = "graph1";
	private static final String graph1IndexName = graph1Name + "NodeNames";

	private static final String graph2Name = "graph2";
	private static final String graph2IndexName = graph2Name + "NodeNames";

	private static IPropertyGraph createSimpleGraph(int nodeCount, int[][] edges) {

		IPropertyGraph graph = new PropertyGraph();

		// Adds simple nodes and edges, where both have a name.
		for (int i = 0; i < nodeCount; i++) {
			IPropertyVertex vertex = new PropertyVertex();
			vertex.setProperty("name", "node" + i);
			graph.addVertex(vertex);
		}
		for (int i = 0; i < edges.length; i++) {
			graph.createEdge(graph.getVertex(edges[i][0]), graph.getVertex(edges[i][1]));
			graph.getEdge(i).setProperty("name", "edge" + i);
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

			// Writes the graph to the database.
			graph1.writeToNeo4j(graphDb, graph1Name, Indexers.indexNodeProperty(graph1IndexName, "name"));
		}
	}

	protected static Node getGraph1Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph1IndexName).query("name", "node" + i).getSingle();
	}

	protected static void writeGraph1ToMySql(Connection connection) throws SQLException {

		// Check if is not already written
		if (MySqlUtil.doesMySqlNameExists(connection, graph1Name) == false) {

			// Creates the graph used in testing.
			IPropertyGraph graph1 = createTestGraph1();

			// Writes the graph to the database.
			graph1.writeToMySql(connection, graph1Name);
		}
	}

	protected static IPropertyGraph overwriteGraph1ToMySql(Connection connection) throws SQLException {

		// Deletes the old graph (object).
		MySqlUtil.deleteMySqlName(connection, graph1Name);
		
		// Creates the graph used in testing.
		IPropertyGraph graph1 = createTestGraph1();

		// Writes the graph to the database.
		graph1.writeToMySql(connection, graph1Name);
		
		return graph1;
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
			IPropertyGraph graph2 = new PropertyGraph();

			// Creates nodes and names the first 10 nodes.
			for (int i = 0; i < count; i++) {
				IPropertyVertex vertex = new PropertyVertex();
				graph2.addVertex(vertex);

				if (i < 10) {
					graph2.getVertex(i).setProperty("name", "node" + i);
				}
			}

			// Creates edges to form a linked list - node 1, node 2, etc.
			for (int i = 0; i < count - 1; i++) {
				graph2.createEdge(graph2.getVertex(i), graph2.getVertex(i+1));
			}

			// Writes the graph to the database.
			graph2.writeToNeo4j(graphDb, graph2Name, Indexers.indexNodeProperty(graph2IndexName, "name"));
		}
	}

	protected static Node getGraph2Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph2IndexName).query("name", "node" + i).getSingle();
	}
}
