package graphFactories;

import java.sql.SQLException;

import exceptions.DataAccessException;

import tests.mySql.MySqlGraphTest;
import graphInterfaces.IPersistentGraph;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;

public class MySqlFactory implements IFactory<MySqlVertex, MySqlEdge> {

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createEmptyGraph(String name) {
		try {
			
			
			// Creates the new graph and clears it.
			IPersistentGraph<MySqlVertex, MySqlEdge> graph = new MySqlGraph(name, MySqlGraphTest.getConnector());
			graph.clear();
			return graph;
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
}
