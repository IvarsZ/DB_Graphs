package graphFactories;

import java.sql.SQLException;


import tests.mySql.MySqlGraphTest;
import util.DataAccessException;
import graphInterfaces.IPersistentGraph;
import mySqlGraph.MySqlConnector;
import mySqlGraph.MySqlEdge;
import mySqlGraph.MySqlGraph;
import mySqlGraph.MySqlVertex;

public class MySqlFactory implements IFactory<MySqlVertex, MySqlEdge> {
	
	MySqlConnector connector;
	
	public MySqlFactory(String db, String user, String password) {
		connector = new MySqlConnector(db, user, password);
	}

	@Override
	public IPersistentGraph<MySqlVertex, MySqlEdge> createGraph(String name) {
		try {
			
			
			return new MySqlGraph(name, connector);
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public String getPrintDetails() {
		return "mysql";
	}
}
