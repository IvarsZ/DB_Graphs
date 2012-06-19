package mySqlGraph;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import graphInterfaces.IIndex;
import graphInterfaces.IIndexManager;

public class MySqlIndexManager implements IIndexManager<MySqlVertex, MySqlEdge> {
	
	private MySqlGraph graph;
	
	private PreparedStatement forVertices;

	protected MySqlIndexManager(MySqlGraph graph) throws SQLException {
		
		this.graph = graph;
		
		Statement st = graph.getMySqlConnection().createStatement();
		try {

			
			// Creates a table for vertex and a table for edge indexes.
			st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getVertexIndexesTableName()  + " (name" + MySqlGraph.MYSQL_STRING + ", PRIMARY_KEY(name))");

			
		} finally {
			st.close();
		}
		
		forVertices = graph.getMySqlConnection().prepareStatement("INSERT IGNORE INTO " + getVertexIndexesTableName() + " (name) VALUES(?)");
	}

	@Override
	public IIndex<MySqlVertex> forVertices(String indexName) {
		
		// executeForVertices(indexName);
		return new MySqlVertexIndex(indexName);
	}

	@Override
	public IIndex<MySqlEdge> forEdges(String indexName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getVertexIndexesTableName() {
		return graph.getName() + "_vertex_indexes";
	}
	
	private void executeForVertices(String indexName) throws SQLException {
		forVertices.setString(1, indexName);
		forVertices.executeUpdate();
	}
}
