package tests.mySql;

import java.sql.SQLException;

import exceptions.DataAccessException;
import graphInterfaces.IPersistentGraph;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;
import tests.abstractTests.TraverserTest;

public class MySqlTraverserTest extends TraverserTest<MySqlVertex, MySqlEdge> {

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			
			return new MySqlGraph(name, MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

}
