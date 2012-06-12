package test;

import java.sql.Connection;
import java.sql.SQLException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import converters.Indexers;
import converters.MySqlConverter;
import converters.Neo4jConverter;

import util.MySqlUtil;
import util.Neo4jUtil;

import graphs.*;
import graphsInterfaces.IEdge;
import graphsInterfaces.IGraph;
import graphsInterfaces.IVertex;


public class TestGraphs {

	public static final String graph1Name = "graph1";
	private static final String graph1IndexName = graph1Name + "NodeNames";

	private static final String graph2Name = "graph2";
	private static final String graph2IndexName = graph2Name + "NodeNames";

	private static IGraph createSimpleGraph(int nodeCount, int[][] edges) {

		IGraph graph = new Graph();

		// Adds simple nodes and edges, where both have a name.
		for (int i = 0; i < nodeCount; i++) {
			IVertex vertex = graph.addVertex(i);
			vertex.getProperties().put("name", "node" + i);
			
		}
		for (int i = 0; i < edges.length; i++) {
			IEdge edge = graph.createEdge(i, graph.getVertex(edges[i][0]), graph.getVertex(edges[i][1]));
			edge.getProperties().put("name", "edge" + i);
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
			IGraph graph1 = createTestGraph1();

			// Writes the graph to the database.
			Neo4jConverter neo4jConverter = new Neo4jConverter();
			neo4jConverter.writeGraph(graphDb, graph1, graph1Name, Indexers.indexNodeProperty(graph1IndexName, "name"));
		}
	}

	protected static Node getGraph1Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph1IndexName).query("name", "node" + i).getSingle();
	}

	protected static void writeGraph1ToMySql(Connection connection) throws SQLException {

		// Check if is not already written
		if (MySqlUtil.doesMySqlNameExists(connection, graph1Name) == false) {

			// Creates the graph used in testing.
			IGraph graph1 = createTestGraph1();

			// Writes the graph to the database.
			MySqlConverter mySqlConverter = new MySqlConverter();
			mySqlConverter.writeGraph(connection, graph1, graph1Name);
		}
	}

	protected static IGraph overwriteGraph1ToMySql(Connection connection) throws SQLException {

		// Deletes the old graph (object).
		MySqlUtil.deleteMySqlName(connection, graph1Name);
		
		// Creates the graph used in testing.
		IGraph graph1 = createTestGraph1();

		// Writes the graph to the database.
		MySqlConverter mySqlConverter = new MySqlConverter();
		mySqlConverter.writeGraph(connection, graph1, graph1Name);
		
		return graph1;
	}

	private static IGraph createTestGraph1() {

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
			IGraph graph2 = new Graph();

			// Creates nodes and names the first 10 nodes.
			for (int i = 0; i < count; i++) {
				IVertex vertex = graph2.addVertex(i);

				if (i < 10) {
					vertex.getProperties().put("name", "node" + i);
				}
			}

			// Creates edges to form a linked list - node 1, node 2, etc.
			for (int i = 0; i < count - 1; i++) {
				graph2.createEdge(i, graph2.getVertex(i), graph2.getVertex(i+1));
			}

			// Writes the graph to the database.
			Neo4jConverter neo4jConverter = new Neo4jConverter();
			neo4jConverter.writeGraph(graphDb, graph2, graph2Name, Indexers.indexNodeProperty(graph2IndexName, "name"));
		}
	}

	protected static Node getGraph2Node(GraphDatabaseService graphDb, int i) {
		return graphDb.index().forNodes(TestGraphs.graph2IndexName).query("name", "node" + i).getSingle();
	}
}
