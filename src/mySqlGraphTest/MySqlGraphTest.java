package mySqlGraphTest;

import java.sql.SQLException;

import mySqlGraph.MySqlConnector;
import mySqlGraph.MySqlGraph;
import graphInterfaces.IPersistentGraph;
import graphTests.PersistentGraphTest;

public class MySqlGraphTest extends PersistentGraphTest {

	private static MySqlConnector getConnector() {
		return new MySqlConnector("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
	}

	@Override
	public IPersistentGraph createGraph() {

		// TODO : can return null.
		try {
			IPersistentGraph graph = new MySqlGraph("mysql_graph_test", getConnector());
			return graph;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
