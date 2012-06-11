package graphs;

import graphsInterfaces.IPropertyEdge;
import graphsInterfaces.IPropertyGraph;
import graphsInterfaces.IPropertyVertex;
import graphsInterfaces.Property;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import util.MySqlUtil;
import util.Neo4jUtil;
import converters.INeo4jIndexer;

/**
 * 
 * Implementation of a property graph that can be converted to Neo4j and MySql.
 * 
 * @author iz2
 *
 */
public final class PropertyGraph implements IPropertyGraph {

	private static final String RELATIONSHIP_TYPE_PROPERTY = "relationshipType";

	public enum GraphRelationshipTypes implements RelationshipType {
		REFERENCE_TO_NODE,
		REFERENCE_TO_GRAPH,
		UNDEFINED
	}

	private ArrayList<IPropertyVertex> vertices;
	private ArrayList<PropertyEdge> edges;

	/**
	 * Creates empty graph with no nodes and edges.
	 */
	public PropertyGraph() {
		vertices = new ArrayList<IPropertyVertex>();
		edges = new ArrayList<PropertyEdge>();
	}
	
	@Override
	public IPropertyVertex getVertex(int index) {
		return vertices.get(index);
	}
	
	@Override
	public void addVertex(IPropertyVertex vertex) {

		// Checks if the vertex is already in the graph.
		if (vertices.contains(vertex) == false) {
			vertices.add(vertex);
		}
	}

	@Override
	public IPropertyEdge createEdge(IPropertyVertex start, IPropertyVertex end) {

		// Checks if both vertices are in the graph.
		if (vertices.contains(start) && vertices.contains(end)) {

			PropertyEdge edge = new PropertyEdge(start, end);
			edges.add(edge);
			return edge;
		}

		return null;
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
			for ( Property<String, String> property : vertices.get(i).getProperties()) {
				insertProperty.setInt(1, i);
				insertProperty.setString(2, property.key);
				insertProperty.setString(3, property.value);
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
			for ( Property<String, String> property : edges.get(i).getProperties()) {
				insertProperty.setInt(1, i);
				insertProperty.setString(2, property.key);
				insertProperty.setString(3, property.value);
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
				for ( Property<String, String> property : vertices.get(i).getProperties()) {
					node.setProperty(property.key, property.value);
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
				for ( Property<String, String> property : edge.getProperties()) {
					relationship.setProperty(property.key, property.value);
				}
				
				tx.success();
			}
			finally {
				tx.finish();
			}
		}

		/*
		 * TODO: improvements
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
	
	private static RelationshipType getRelationshipTypeOfEdge(PropertyEdge edge) {
		
		// Gets the relationship type property.
		String relationshipType = edge.getProperty(RELATIONSHIP_TYPE_PROPERTY);
		
		// The relationship type is undefined.
		if (relationshipType == null) {
			return GraphRelationshipTypes.UNDEFINED;
		}
		
		return DynamicRelationshipType.withName(relationshipType);
	}

	@Override
	public void readFromMySql(Connection connection, String name)
			throws SQLException {
		// TODO : Implement
		
	}

	@Override
	public void readFromNeo4j(GraphDatabaseService graphDb, String rootName) {
		// TODO : Implement
		
	}
}
