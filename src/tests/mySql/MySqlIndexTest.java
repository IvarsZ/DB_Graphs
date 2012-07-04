package tests.mySql;

import java.sql.SQLException;

import tests.abstractTests.IndexTest;
import util.DataAccessException;

import mySqlGraph.MySqlConnector;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;
import graphInterfaces.IPersistentGraph;

public class MySqlIndexTest extends IndexTest<MySqlVertex, MySqlEdge> {

	protected static MySqlConnector getConnector() {
		return new MySqlConnector("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
	}

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph() {
		try {
			
			
			// Creates and returns new graph.
			return new MySqlGraph("mysql_index_test", getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
