package mySqlGraphTest;

import java.sql.SQLException;

import exceptions.DataAccessException;

import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlGraphOperator;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphTests.GraphOperatorTest;

public class MySqlGraphOperatorTest extends GraphOperatorTest {
	
	@Override
	public IPersistentGraph createGraph() {
		try {
			
			
			return new MySqlGraph("mysql_graph_operator_test", MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public IGraphOperator createOperator(IPersistentGraph graph) {
		try {
			
			
			return new MySqlGraphOperator(graph);
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

}
