package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import converters.Indexers;
import converters.MySqlConverter;
import converters.Neo4jConverter;

import util.MySqlUtil;
import util.Neo4jUtil;

import graph.Graph;
import graphInterfaces.*;


public class TestGraphs {

	public static final String graph1Name = "graph1";
	private static final String graph1IndexName = graph1Name + "NodeNames";

	private static final String graph2Name = "graph2";
	private static final String graph2IndexName = graph2Name + "NodeNames";

	private static IGraph createSimpleGraph(int nodeCount, int[][] edges) {

		IGraph graph = new Graph();

		// Adds simple nodes and edges, where both have a name.
		Map<Integer, IVertex> vertices = new HashMap<Integer, IVertex>(nodeCount);
		for (int i = 0; i < nodeCount; i++) {
			IVertex vertex = graph.createVertex();
			vertex.setProperty("name", "node" + i);
		}
		for (int i = 0; i < edges.length; i++) {
			IEdge edge = graph.createEdge(vertices.get(edges[i][0]), vertices.get(edges[i][1]));
			edge.setProperty("name", "edge" + i);
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
			
			

			// Creates vertices and names the first 10 vertices, and links two subsequent vertices.
			IVertex vertexPrev = graph2.createVertex();
			vertexPrev.setProperty("name", "node" + 0);
			
			for (int i = 1; i < count; i++) {
				IVertex vertexCur = graph2.createVertex();
				
				if (i < 10) {
					vertexCur.setProperty("name", "node" + i);
				}
				
				graph2.createEdge(vertexPrev, vertexCur);
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
