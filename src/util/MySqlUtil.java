package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlUtil {

	public static final String MYSQL_STRING = " VARCHAR(50) ";
	
	public static boolean doesMySqlNameExists(Connection connection, String name) throws SQLException {
		
		// Checks if the name is written in the names table.
		Statement st = connection.createStatement();
		st.executeUpdate("CREATE TABLE IF NOT EXISTS taken_names (name" + MYSQL_STRING + ")");
		ResultSet rs = st.executeQuery("SELECT name FROM taken_names WHERE name = '" + name + "'");

		if (rs.next() == true) {

			// The name is already written.
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * Attempts to register a name for identifying this graph in MySql.
	 * 
	 * @param connection - connection to MySql database.
	 * @param name - the name of this graph in MySql.
	 * 
	 * @return true if the name was successfully registered, false if the name is already taken.
	 * 
	 * @throws SQLException
	 */
	public static boolean registerMySqlName(Connection connection, String name) throws SQLException {

		// Checks if the name is written in the names table.
		if (doesMySqlNameExists(connection, name) == true) {
			return false;
		}
		
		// Writes the name.
		Statement st = connection.createStatement();
		st.executeUpdate("INSERT INTO taken_names (name) VALUES ('" + name + "')");
		return true;
	}
}
