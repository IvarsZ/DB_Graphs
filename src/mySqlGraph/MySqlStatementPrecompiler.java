package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class MySqlStatementPrecompiler {
	
	private PreparedStatement getProperty;

	protected MySqlStatementPrecompiler(MySqlGraph graph) throws SQLException {
		
		getProperty = graph.getMySqlConnection().prepareStatement("SELECT p_value FROM " + graph.getEdgesPropertiesTableName() + "WHERE id = ?, p_key = ?");
	}
	
	protected PreparedStatement getGetPropertyStatement() {
		return getProperty;
	}
	
}
