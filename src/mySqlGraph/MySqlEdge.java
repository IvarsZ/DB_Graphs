package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlEdge implements IEdge {

	private MySqlVertex start;
	private MySqlVertex end;
	private MySqlGraph graph;

	private long id;

	/**
	 * 
	 * Creates a MySqlEdge, but it and its start and end vertices have to be already written in the MySql database.
	 * 
	 * @param start
	 * @param end
	 * 
	 * @param graph
	 * 
	 * @throws IllegalArgumentException - if at least one of the vertices is null.
	 */
	protected MySqlEdge(long id, MySqlVertex start, MySqlVertex end, MySqlGraph graph) throws IllegalArgumentException {
		
		// Start and end vertices can't be null.
		if (start == null || end == null) {
			throw new IllegalArgumentException();
		}

		this.id = id;
		this.start = start;
		this.end = end;
		this.graph = graph;
	}

	@Override
	public void setProperty(String key, String value) throws DataAccessException {
		try {
			
			
			graph.getStatements().executeSetEdgeProperty(id, key, value);

			
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
			rs = graph.getStatements().executeGetEdgeProperty(id, key);

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
	public IVertex getStart() {
		return start;
	}

	@Override
	public IVertex getEnd() {
		return end;
	}

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof MySqlEdge) {
			
			// TODO : check graphs.
			MySqlEdge edge = (MySqlEdge) obj;
			return id == edge.id;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}

}
