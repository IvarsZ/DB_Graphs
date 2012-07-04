package tests.mySql;

import graphInterfaces.IPersistentGraph;

import java.sql.SQLException;

import tests.abstractTests.NeighbourFinderTest;
import util.DataAccessException;

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
