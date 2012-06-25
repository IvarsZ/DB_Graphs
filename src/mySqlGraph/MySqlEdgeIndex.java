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
class MySqlEdgeIndex implements IIndex<MySqlEdge>{

	private MySqlGraph graph;

	private PreparedStatement addToIndex;
	private PreparedStatement getFromIndex;

	protected MySqlEdgeIndex(String tableName, MySqlGraph graph) throws SQLException {
		Statement st = graph.getMySqlConnection().createStatement();
		try {

			// Creates a table for indexing and indexes it.
			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (p_key" + MySqlGraph.MYSQL_STRING + ", p_value" + MySqlGraph.MYSQL_STRING + ", id BIGINT, PRIMARY KEY(p_key, p_value, id))");
			st.executeUpdate("ALTER TABLE " + tableName + " ADD INDEX(p_key, p_value)");

		} finally {
			st.close();
		}

		this.graph = graph;

		// Prepares statements.
		addToIndex = graph.getMySqlConnection().prepareStatement("INSERT INTO " + tableName + " (p_key, p_value, id) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE p_key = p_key");
		getFromIndex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE p_key = ? AND p_value = ?");
	}

	@Override
	public void add(MySqlEdge edge, String key, String value) {
		try {


			// Adds the key, value, id triple to the index table.
			executeAddToIndex(key, value, edge.getId());


		} catch (SQLException e) {
			throw new DataAccessException(e);
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
}
