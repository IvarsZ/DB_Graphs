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

	protected MySqlEdge(MySqlVertex start, MySqlVertex end, MySqlGraph graph) {
		this.start = start;
		this.end = end;
		this.graph = graph;
	}

	@Override
	public void setProperty(String key, String value) {
		// TODO Auto-generated method stub

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
			PreparedStatement st = graph.getStatements().getGetPropertyStatement();
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
			return null;
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

}
