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
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph() {
		try {
			
			
			return new MySqlGraph("mysql_graph_operator_test", MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
