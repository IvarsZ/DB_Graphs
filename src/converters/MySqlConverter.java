package converters;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
		
		// TODO : fix long ids.

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
		for (IVertex vertex : graph.getVertices()) {

			// writes the vertex,
			insertNode.setInt(1, vertex.getId());
			insertNode.executeUpdate();

			// and its properties.
			for (String key : vertex.getKeys()) {
				insertProperty.setInt(1, vertex.getId());
				insertProperty.setString(2, key);
				insertProperty.setString(3, vertex.getProperty(key));
				insertProperty.executeUpdate();
			}
		}

		// For all edges in the graph.
		PreparedStatement insertEdge = connection.prepareStatement("INSERT INTO " + edgesTableName + " (id, start, end) VALUES(?, ?, ?)");
		insertProperty = connection.prepareStatement("INSERT INTO " + edgesPropertiesTableName + " (id, p_key, p_value) VALUES(?, ?, ?)");
		for (IEdge edge : graph.getEdges()) {

			// Gets the id of the start and the end node.
			int start = edge.getStart().getId();
			int end = edge.getEnd().getId();

			// Writes the edge,
			
			insertEdge.setInt(1, edge.getId());
			insertEdge.setInt(2, start);
			insertEdge.setInt(3, end);
			insertEdge.executeUpdate();

			// and its properties.
			for (String key : edge.getKeys()) {
				insertProperty.setInt(1, edge.getId());
				insertProperty.setString(2, key);
				insertProperty.setString(3, edge.getProperty(key));
				insertProperty.executeUpdate();
			}
		}
	}
}
