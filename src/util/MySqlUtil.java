package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlUtil {

	public static final String MYSQL_STRING = " VARCHAR(50) ";
	
	/**
	 * 
	 * Checks if a specified name of an object (graph) is already used in MySql database.
	 * 
	 * @param connection - to the MySql database.
	 * @param name - the name used to identify the object.
	 * 
	 * @return true if the name is already used (exists), false otherwise.
	 * 
	 * @throws SQLException
	 */
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
	
	public static void deleteMySqlName(Connection connection, String name) throws SQLException {
		
		// Deletes the name written in the names table.
		Statement st = connection.createStatement();
		st.executeUpdate("CREATE TABLE IF NOT EXISTS taken_names (name" + MYSQL_STRING + ")");
		st.executeUpdate("DELETE FROM taken_names WHERE name = '" + name + "'");
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
