package graphs;

import graphsInterfaces.IEdge;
import graphsInterfaces.IGraph;
import graphsInterfaces.IVertex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import util.MySqlUtil;
import util.Neo4jUtil;
import convertersInterfaces.INeo4jIndexer;

/**
 * 
 * Implementation of a property graph that can be converted to Neo4j and MySql.
 * 
 * @author iz2
 *
 */
public final class PropertyGraph implements IGraph {

	/**
	 * 
	 * Relationship types used when writing to ne4oj.
	 *
	 */
	public enum GraphRelationshipTypes implements RelationshipType {
		REFERENCE_TO_NODE,
		REFERENCE_TO_GRAPH,
		UNDEFINED
	}
	
	/**
	 * String representing the name of the relationship type property.
	 */
	private static final String RELATIONSHIP_TYPE_PROPERTY = "relationshipType";
	
	/**
	 * 
	 * @return the relationship type in Neo4j of the specified edge.
	 * 
	 */
	private static RelationshipType getRelationshipTypeOfEdge(PropertyEdge edge) {
		
		// Gets the relationship type property.
		String relationshipType = edge.getProperties().get(RELATIONSHIP_TYPE_PROPERTY);
		
		// The relationship type is undefined.
		if (relationshipType == null) {
			return GraphRelationshipTypes.UNDEFINED;
		}
		
		return DynamicRelationshipType.withName(relationshipType);
	}

	private ArrayList<IVertex> vertices;
	private ArrayList<PropertyEdge> edges;

	/**
	 * Creates empty graph with no nodes and edges.
	 */
	public PropertyGraph() {
		vertices = new ArrayList<IVertex>();
		edges = new ArrayList<PropertyEdge>();
	}
	
	@Override
	public void addVertex(IVertex vertex) {

		// Checks if the vertex is already in the graph.
		if (vertices.contains(vertex) == false) {
			vertices.add(vertex);
		}
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end) {

		// Checks if both vertices are in the graph.
		if (vertices.contains(start) && vertices.contains(end)) {

			PropertyEdge edge = new PropertyEdge(start, end);
			edges.add(edge);
			return edge;
		}

		return null;
	}
	
	@Override
	public IVertex getVertex(int index) {
		return vertices.get(index);
	}
	
	@Override
	public IEdge getEdge(int i) {
		return edges.get(i);
	}

	@Override
	public void writeToMySql(Connection connection, String name) throws SQLException {

		// Attempts to register the graphs name in the database.
		if (MySqlUtil.registerMySqlName(connection, name) == false) {

			// Couldn't register the name, so doesn't write anything.
			return;
		}

		// Names of the tables used for representing the graph.
		String nodesTableName = name + "_nodes";
		String nodesPropertiesTableName = name + "_nodes_properties";
		String edgesTableName = name + "_edges";
		String edgesPropertiesTableName = name + "_edges_properties";
		
		// Creates the tables for representing the graph.
		Statement st = connection.createStatement();
		st.executeUpdate("DROP TABLE IF EXISTS " + nodesTableName);
		st.executeUpdate("CREATE TABLE "  + nodesTableName + " (id INT UNSIGNED NOT NULL, PRIMARY KEY(id))");
		st.executeUpdate("DROP TABLE IF EXISTS " + nodesPropertiesTableName);
		st.executeUpdate("CREATE TABLE "  + nodesPropertiesTableName + 
				" (id INT UNSIGNED NOT NULL, PRIMARY KEY(id), p_key" + MySqlUtil.MYSQL_STRING + "NOT NULL, p_value" + MySqlUtil.MYSQL_STRING + "NOT NULL)");
		st.executeUpdate("DROP TABLE IF EXISTS " + edgesTableName);
		st.executeUpdate("CREATE TABLE " + edgesTableName + " (id INT UNSIGNED NOT NULL, PRIMARY KEY(id), start INT, end INT)");
		st.executeUpdate("DROP TABLE IF EXISTS " + edgesPropertiesTableName);
		st.executeUpdate("CREATE TABLE "  + edgesPropertiesTableName + 
				" (id INT UNSIGNED NOT NULL, PRIMARY KEY(id), p_key" + MySqlUtil.MYSQL_STRING + "NOT NULL, p_value" + MySqlUtil.MYSQL_STRING + "NOT NULL)");

		// For all vertices in the graph,
		PreparedStatement insertNode = connection.prepareStatement("INSERT INTO " + nodesTableName + " (id) VALUES(?)");
		PreparedStatement insertProperty = connection.prepareStatement("INSERT INTO " + nodesPropertiesTableName + " (id, p_key, p_value) VALUES(?, ?, ?)");
		for (int i = 0; i < vertices.size(); i++) {

			// writes the vertex,
			insertNode.setInt(1, i);
			insertNode.executeUpdate();

			// and its properties.
			for ( Entry<String, String> property : vertices.get(i).getProperties().entrySet()) {
				insertProperty.setInt(1, i);
				insertProperty.setString(2, property.getKey());
				insertProperty.setString(3, property.getValue());
				insertProperty.executeUpdate();
			}
		}

		// For all edges in the graph.
		PreparedStatement insertEdge = connection.prepareStatement("INSERT INTO " + edgesTableName + " (id, start, end) VALUES(?, ?, ?)");
		insertProperty = connection.prepareStatement("INSERT INTO " + edgesPropertiesTableName + " (id, p_key, p_value) VALUES(?, ?, ?)");
		for (int i = 0; i < edges.size(); i++) {

			// Gets the id of the start and the end node.
			int start = vertices.indexOf(edges.get(i).getStart());
			int end = vertices.indexOf(edges.get(i).getEnd());

			// Writes the edge,
			insertEdge.setInt(1, i);
			insertEdge.setInt(2, start);
			insertEdge.setInt(3, end);
			insertEdge.executeUpdate();

			// and its properties.
			for ( Entry<String, String> property : edges.get(i).getProperties().entrySet()) {
				insertProperty.setInt(1, i);
				insertProperty.setString(2, property.getKey());
				insertProperty.setString(3, property.getValue());
				insertProperty.executeUpdate();
			}
		}
	}
	
	@Override
	public void writeToNeo4j(GraphDatabaseService graphDb, String rootName, INeo4jIndexer indexer) {

		// Attempts to register a root node with the rootName.
		Node root = Neo4jUtil.registerNeo4jRoot(graphDb, rootName);
		if (root == null) {

			// Couldn't register the root, so doesn't write anything.
			return;
		}

		// For all vertices in the graph,
		Transaction tx;
		Node[] nodes = new Node[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {

			tx = graphDb.beginTx();
			try {
				
				// creates a neo4j node with the properties of the vertex,
				Node node = graphDb.createNode();
				for ( Entry<String, String> property : vertices.get(i).getProperties().entrySet()) {
					node.setProperty(property.getKey(), property.getValue());
				}
				
				// indexes the node.
				indexer.indexNode(node);
				
				// connects the node to the root and saves it in the nodes array.
				root.createRelationshipTo(node, GraphRelationshipTypes.REFERENCE_TO_NODE);
				nodes[i] = node;

				tx.success();
			}
			finally {
				tx.finish();
			}
		}

		// For all edges in the graph,
		for (PropertyEdge edge : edges) {
			
			// Gets the neo4j node of the start and the end vertex.
			Node start = nodes[vertices.indexOf(edge.getStart())];
			Node end = nodes[vertices.indexOf(edge.getEnd())];

			tx = graphDb.beginTx();
			try {
				
				// creates a neo4j relationship with the properties of the edge,
				Relationship relationship = start.createRelationshipTo(end, getRelationshipTypeOfEdge(edge));
				for ( Entry<String, String> property : edge.getProperties().entrySet()) {
					relationship.setProperty(property.getKey(), property.getValue());
				}
				
				tx.success();
			}
			finally {
				tx.finish();
			}
		}

		/*
		 * TODO: Neo4j writer improvements
		 * 
		 * 	connect only unconnected parts, not every node.
		 * 
		 * 	write using BFS.
		 * 
		 * 	are references actually required, problem that the neighbour methods now have to know
		 * about the references
		 * 
		 */
	}
	
	/**
	 * Deletes all nodes and edges.
	 */
	private void clear() {
		vertices = new ArrayList<IVertex>();
		edges = new ArrayList<PropertyEdge>();
	}

	@Override
	public void readFromMySql(Connection connection, String name)
			throws SQLException {

		// Clears the old graph.
		clear();
		
		// Checks that the graph exists in the database.
		if (MySqlUtil.doesMySqlNameExists(connection, name)) {
			
			// Names of the tables used for representing the graph.
			String nodesTableName = name + "_nodes";
			String nodesPropertiesTableName = name + "_nodes_properties";
			String edgesTableName = name + "_edges";
			String edgesPropertiesTableName = name + "_edges_properties";
			
			// Reads and creates vertices.
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM " + nodesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				
				// Makes sure can add the vertex.
				while (vertices.size() <= id) {
					vertices.add(null);
				}
				vertices.set(id, new PropertyVertex());
			}
			
			// Reads and adds vertex properties.
			rs = st.executeQuery("SELECT * FROM " + nodesPropertiesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				String key = rs.getString("p_key");
				String value = rs.getString("p_value");
				vertices.get(id).getProperties().put(key, value);
			}
			
			// Reads and creates edges.
			rs = st.executeQuery("SELECT * FROM " + edgesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				int start = rs.getInt("start");
				int end = rs.getInt("end");
				
				// Makes sure can add the edge.
				while (edges.size() <= id) {
					edges.add(null);
				}
				edges.set(id, new PropertyEdge(vertices.get(start), vertices.get(end)));
			}
			
			// Reads and adds edge properties.
			rs = st.executeQuery("SELECT * FROM " + edgesPropertiesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				String key = rs.getString("p_key");
				String value = rs.getString("p_value");
				edges.get(id).getProperties().put(key, value);
			}
		}
	}

	@Override
	public void readFromNeo4j(GraphDatabaseService graphDb, String rootName) {
		// TODO : Implement

	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof PropertyGraph) {
			
			PropertyGraph graph2 = (PropertyGraph) obj;
			
			// Checks vertices.
			if (graph2.vertices.size() != vertices.size()) {
				return false;
			}
			for (int i = 0; i < vertices.size(); i++) {

				// Check properties.
				if (vertices.get(i).getProperties().equals(graph2.vertices.get(i).getProperties()) == false) {
					return false;
				}
			}
			
			// Checks edges.
			if (graph2.edges.size() != edges.size()) {
				return false;
			}
			for (int i = 0; i < edges.size(); i++) {
				
				// Checks indexes of start and end of edges.
				if (vertices.indexOf(edges.get(i).start) != graph2.vertices.indexOf(graph2.edges.get(i).start) ||
					vertices.indexOf(edges.get(i).end) != graph2.vertices.indexOf(graph2.edges.get(i).end)) {
					return false;
				}
				
				// Check properties.
				if (edges.get(i).getProperties().equals(graph2.edges.get(i).getProperties()) == false) {
					return false;
				}
			}
		}
		else {
			return false;
		}
		
		// All nodes and edges were equal.
		return true;
	}
}
