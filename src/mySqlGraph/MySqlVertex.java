package mySqlGraph;

import graphInterfaces.IVertex;

import java.sql.PreparedStatement;
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
	public void setProperty(String key, String value) {

		try {

			// Makes the update statement and executes it.
			PreparedStatement st = graph.getStatements().getSetVertexProperty();
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
			PreparedStatement st = graph.getStatements().getGetVertexProperty();
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
	public long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		// TODO : full property check.
		
		if (obj instanceof MySqlVertex) {
			MySqlVertex vertex = (MySqlVertex) obj;
			return id == vertex.id;
		}
		
		return false;
	}

}
