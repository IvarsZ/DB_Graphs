package mySqlGraphTest;

import java.sql.SQLException;

import mySqlGraph.MySqlConnector;
import mySqlGraph.MySqlGraph;
import graphInterfaces.IGraph;
import graphTests.GraphTest;

public class MySqlGraphTest extends GraphTest {
	
	private static MySqlConnector getConnector() {
		return new MySqlConnector("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
	}

	@Override
	public IGraph createEmptyGraph() {
		
		// TODO : can return null.
		try {
			IGraph graph = new MySqlGraph("mysql_graph_test", getConnector());
			graph.clear();
			return graph;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
