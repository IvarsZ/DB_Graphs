package tests.mySql;

import java.sql.SQLException;

import graphInterfaces.IPersistentGraph;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;
import tests.abstractTests.AncestorFinderTest;
import util.DataAccessException;

public class MySqlAncestorFinderTest extends AncestorFinderTest<MySqlVertex, MySqlEdge> {

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			// TODO : move the connector, and create a factory???
			return new MySqlGraph(name, MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
