package mySqlGraphTest;

import exceptions.DataAccessException;
import graphInterfaces.IPersistentGraph;
import graphTests.GraphOperatorTest;

import java.sql.SQLException;

import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;

public class MySqlGraphOperatorTest extends GraphOperatorTest<MySqlVertex, MySqlEdge> {
	
	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			
			return new MySqlGraph(name, MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
