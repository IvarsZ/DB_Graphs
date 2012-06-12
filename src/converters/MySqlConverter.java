package converters;

import graphs.Graph;
import graphsInterfaces.IEdge;
import graphsInterfaces.IGraph;
import graphsInterfaces.IVertex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import util.MySqlUtil;
import convertersInterfaces.IMySqlConverter;

/**
 * 
 * Class for converting objects to and from MySql.
 * 
 * Currently can write and read a graph to and from MySql.
 * 
 * @author iz2
 *
 */
public class MySqlConverter implements IMySqlConverter {

	@Override
	public void writeGraph(Connection connection, IGraph graph, String name) throws SQLException {

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
		for (Entry<Integer, IVertex> vertex : graph.getVertices()) {

			// writes the vertex,
			insertNode.setInt(1, vertex.getKey());
			insertNode.executeUpdate();

			// and its properties.
			for (Entry<String, String> property : vertex.getValue().getProperties().entrySet()) {
				insertProperty.setInt(1, vertex.getKey());
				insertProperty.setString(2, property.getKey());
				insertProperty.setString(3, property.getValue());
				insertProperty.executeUpdate();
			}
		}

		// For all edges in the graph.
		PreparedStatement insertEdge = connection.prepareStatement("INSERT INTO " + edgesTableName + " (id, start, end) VALUES(?, ?, ?)");
		insertProperty = connection.prepareStatement("INSERT INTO " + edgesPropertiesTableName + " (id, p_key, p_value) VALUES(?, ?, ?)");
		for (Entry<Integer, IEdge> edge : graph.getEdges()) {

			// Gets the id of the start and the end node.
			int start = edge.getValue().getStart().getId();
			int end = edge.getValue().getEnd().getId();

			// Writes the edge,
			insertEdge.setInt(1, edge.getKey());
			insertEdge.setInt(2, start);
			insertEdge.setInt(3, end);
			insertEdge.executeUpdate();

			// and its properties.
			for (Entry<String, String> property : edge.getValue().getProperties().entrySet()) {
				insertProperty.setInt(1, edge.getKey());
				insertProperty.setString(2, property.getKey());
				insertProperty.setString(3, property.getValue());
				insertProperty.executeUpdate();
			}
		}
	}

	@Override
	public IGraph readGraph(Connection connection, String name)
			throws SQLException {

		// Clears the new graph.
		IGraph graph = new Graph();
		
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
				graph.addVertex(id);
			}
			
			// Reads and adds vertex properties.
			rs = st.executeQuery("SELECT * FROM " + nodesPropertiesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				String key = rs.getString("p_key");
				String value = rs.getString("p_value");
				graph.getVertex(id).getProperties().put(key, value);
			}
			
			// Reads and creates edges.
			rs = st.executeQuery("SELECT * FROM " + edgesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				int start = rs.getInt("start");
				int end = rs.getInt("end");
				
				graph.createEdge(id, graph.getVertex(start), graph.getVertex(end));
			}
			
			// Reads and adds edge properties.
			rs = st.executeQuery("SELECT * FROM " + edgesPropertiesTableName);
			while (rs.next()) {
				int id = rs.getInt("id");
				String key = rs.getString("p_key");
				String value = rs.getString("p_value");
				graph.getEdge(id).getProperties().put(key, value);
			}
		}
		
		return graph;
	}

}
