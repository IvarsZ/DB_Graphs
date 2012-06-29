package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IIndex;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;




/**
 * 
 * IIndex implementation for MySql vertices.
 * 
 * @author iz2
 *
 */
class MySqlVertexIndex implements IIndex<MySqlVertex> {

	private MySqlGraph graph;

	private PreparedStatement addToIndex;
	private PreparedStatement getFromIndex;


	protected MySqlVertexIndex(String tableName, MySqlGraph graph) throws SQLException {
		Statement st = graph.getMySqlConnection().createStatement();
		try {


			// Creates a table for indexing and indexes it.
			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (p_key" + MySqlGraph.MYSQL_STRING + ", p_value" + MySqlGraph.MYSQL_STRING + ", id BIGINT, PRIMARY KEY(p_key, p_value, id))");
			// Primary keys are automatically indexed.
			//st.executeUpdate("ALTER TABLE " + tableName + " ADD INDEX(p_key, p_value)");


		} finally {
			st.close();
		}

		this.graph = graph;

		// Prepares statements.
		addToIndex = graph.getMySqlConnection().prepareStatement("INSERT INTO " + tableName + " (p_key, p_value, id) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE p_key = p_key");
		getFromIndex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + tableName + " WHERE p_key = ? AND p_value = ?");
	}

	@Override
	public void add(MySqlVertex vertex, String key, String value) {
		try {


			// Adds the key, value, id triple to the index table.
			executeAddToIndex(key, value, vertex.getId());


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public Iterable<MySqlVertex> get(final String key, final String value) {
		return new Iterable<MySqlVertex>() {

			@Override
			public Iterator<MySqlVertex> iterator() {
				try {

					// Creates vertex iterator from the rows that have the same key and value.
					return new MySqlVertexIterator(executeGetFromIndex(key, value), graph);


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
	
	@Override
	public MySqlVertex getFirst(String key, String value) {
		// TODO : closure.
		try {
			
			
			MySqlVertexIterator vertices = new MySqlVertexIterator(executeGetFromIndex(key, value), graph);
			if (vertices.hasNext()) {
				return vertices.next();
			}
			
			return null;
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

}
