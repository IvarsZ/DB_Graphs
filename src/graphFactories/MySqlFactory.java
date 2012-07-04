package graphFactories;

import java.sql.SQLException;


import tests.mySql.MySqlGraphTest;
import util.DataAccessException;
import graphInterfaces.IPersistentGraph;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;

public class MySqlFactory implements IFactory<MySqlVertex, MySqlEdge> {

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			
			return new MySqlGraph(name, MySqlGraphTest.getConnector());
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public String getPrintDetails() {
		return "mysql";
	}
}
