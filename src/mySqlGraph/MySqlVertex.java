package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IVertex;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlVertex implements IVertex {

	private long id;
	private MySqlGraph graph;
	
	/**
	 * 
	 * Creates a MySqlVertex, but it has to be already written in the MySql database.
	 * 
	 * @param id - id of the vertex.
	 * @param graph - MySqlGraph to which the vertex belongs to.
	 * 
	 * @throws SQLException
	 */
	protected MySqlVertex(long id, MySqlGraph graph) {
		
		this.id = id;
		this.graph = graph;
	}

	@Override
	public void setProperty(String key, String value) throws DataAccessException {
		try {

			
			// Executes the update statement.
			graph.getStatements().executeSetVertexProperty(id, key, value);
			

		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public Iterable<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String key) throws DataAccessException {
		ResultSet rs = null;
		try {

			
			// Makes the query.
			rs = graph.getStatements().executeGetVertexProperty(id, key);

			// If there was a result returns it, otherwise null.
			if (rs.next()) {
				return rs.getString(1);
			}
			else {
				return null;
			}
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { /* Ignore */ };
		}
	}

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		// TODO : check graphs as well.
		
		if (obj instanceof MySqlVertex) {
			MySqlVertex vertex = (MySqlVertex) obj;
			return id == vertex.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}

}
