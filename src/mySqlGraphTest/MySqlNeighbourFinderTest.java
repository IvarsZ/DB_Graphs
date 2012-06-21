package mySqlGraphTest;

import exceptions.DataAccessException;
import graphInterfaces.IPersistentGraph;
import graphTests.NeighbourFinderTest;

import java.sql.SQLException;

import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;

public class MySqlNeighbourFinderTest extends NeighbourFinderTest<MySqlVertex, MySqlEdge> {
	
	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			
			return new MySqlGraph(name, MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
