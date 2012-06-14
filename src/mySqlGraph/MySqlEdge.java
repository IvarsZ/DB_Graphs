package mySqlGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

import java.sql.PreparedStatement;
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
	 * @throws SQLException
	 */
	protected MySqlEdge(long id, MySqlVertex start, MySqlVertex end, MySqlGraph graph) throws SQLException {

		this.id = id;
		this.start = start;
		this.end = end;
		this.graph = graph;
	}

	@Override
	public void setProperty(String key, String value) {

		try {

			// Makes the update statement and executes it.
			PreparedStatement st = graph.getStatements().getSetEdgeProperty();
			st.setLong(1, id);
			st.setString(2, key);
			st.setString(3, value);
			st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Iterable<String> getKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String key) {

		try {

			// Makes the query.
			PreparedStatement st = graph.getStatements().getGetEdgeProperty();
			st.setLong(1, id);
			st.setString(2, key);
			ResultSet rs = st.executeQuery();

			// If there was a result returns it, otherwise null.
			if (rs.next()) {
				return rs.getString("p_value");
			}
			else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // TODO : better error handling mechanism, should interface allow exceptions?
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
			
			// TODO : check properties (or better graphs).
			MySqlEdge edge = (MySqlEdge) obj;
			return id == edge.id;
		}
		
		return false;
	}

}
