package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import exceptions.DataAccessException;

import graphInterfaces.IIndex;

/**
 * 
 * IIndex implementation for MySql edges.
 * 
 * @author iz2
 *
 */
public class MySqlEdgeIndex implements IIndex<MySqlEdge>{
	
	private MySqlGraph graph;

	private PreparedStatement addToIndex;
	private PreparedStatement getFromIndex;
	private PreparedStatement getDuplicateFromIndex;

	protected MySqlEdgeIndex(String tableName, MySqlGraph graph) throws SQLException {
		Statement st = graph.getMySqlConnection().createStatement();
		try {
			
			
			// Creates a table for indexing and indexes it.
			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (p_key" + MySqlGraph.MYSQL_STRING + ", p_value" + MySqlGraph.MYSQL_STRING + ", id BIGINT)");
			st.executeUpdate("ALTER TABLE " + tableName + " ADD INDEX(p_key, p_value)");
			
			
		} finally {
			st.close();
		}
		
		this.graph = graph;

		// Prepares statements.
		addToIndex = graph.getMySqlConnection().prepareStatement("INSERT INTO " + tableName + " (p_key, p_value, id) VALUES(?, ?, ?)");
		getFromIndex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE p_key = ? AND p_value = ?");
		getDuplicateFromIndex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE p_key = ? AND p_value = ? AND id = ?");
	}
	
	@Override
	public void add(MySqlEdge edge, String key, String value) {
		ResultSet duplicates = null;
		try {
			

			// Checks that there are no duplicates.
			duplicates = executeGetDuplicateFromIndex(key, value, edge.getId());
			if (duplicates.next() == false) {

				// Adds the key, value, id triple to the index table.
				executeAddToIndex(key, value, edge.getId());
			}
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			if (duplicates != null) try { duplicates.close(); } catch (SQLException e) { /* IGNORE */ }
		}
	}

	@Override
	public Iterable<MySqlEdge> get(final String key, final String value) {
		return new Iterable<MySqlEdge>() {

			@Override
			public Iterator<MySqlEdge> iterator() {
				try {

					// Creates vertex iterator from the rows that have the same key and value.
					return new MySqlEdgeIterator(executeGetFromIndex(key, value), graph);


				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}

		};
	}
	
	private void executeAddToIndex(String key, String value, long id) throws SQLException {
		addToIndex.setString(1, key);
		addToIndex.setString(2, value);
		addToIndex.setLong(3, id);
		addToIndex.executeUpdate();
	}

	private ResultSet executeGetFromIndex(String key, String value) throws SQLException {
		getFromIndex.setString(1, key);
		getFromIndex.setString(2, value);
		return getFromIndex.executeQuery();
	}

	private ResultSet executeGetDuplicateFromIndex(String key, String value, long id) throws SQLException {
		getDuplicateFromIndex.setString(1, key);
		getDuplicateFromIndex.setString(2, value);
		getDuplicateFromIndex.setLong(3, id);
		return getDuplicateFromIndex.executeQuery();
	}
}
