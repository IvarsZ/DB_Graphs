package mySqlGraphTest;

import java.sql.SQLException;

import exceptions.DataAccessException;

import mySqlGraph.MySqlConnector;
import mySqlGraph.MySqlGraph;
import graphInterfaces.IPersistentGraph;
import graphTests.PersistentGraphTest;

public class MySqlGraphTest extends PersistentGraphTest {

	protected static MySqlConnector getConnector() {
		return new MySqlConnector("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
	}

	@Override
	public IPersistentGraph createGraph() {
		try {
			
			
			// Creates and returns new graph.
			return new MySqlGraph("mysql_graph_test", getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
