package util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import mySqlGraph.MySqlConnector;
import tests.mySql.MySqlGraphTest;

public class Util {
	
	/**
	 * 
	 * Performs the modulo operation, a mod m.
	 *
	 * @return a mod m, where the result is nonnegative.
	 */
	public static long mod(long a, long m) {
		
		long r = a % m;
		if (r < 0) {
			r += m;
		}
		
		return r;
	}
	
	private static void dropAllTables() throws SQLException {
		
		MySqlConnector connector = MySqlGraphTest.getConnector();
		Connection con = connector.connect();
		
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("SHOW TABLES");
		
		ArrayList<String> tableNames = new ArrayList<String>();
		while(rs.next()) {
			tableNames.add(rs.getString(1));
		}
		
		for (String tableName : tableNames) {
			st.executeUpdate("DROP TABLE " + tableName);
		}
	}
	
	public static void main(String[] args) throws SQLException {
		dropAllTables();
	}

}
