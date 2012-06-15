package mySqlGraph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnector {

	private String url;
	private String user;
	private String password;


	public MySqlConnector(String url, String user, String password) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
	}

	protected Connection connect() throws SQLException {

		// Opens a connection to a mySql database from parameters.
		return DriverManager.getConnection(url, user, password);
	}
}