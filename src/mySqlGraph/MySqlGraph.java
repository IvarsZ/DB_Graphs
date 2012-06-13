package mySqlGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import util.MySqlUtil;

public class MySqlGraph implements IGraph {

	private String graphName;

	private Connection mySql;
	
	private MySqlStatementPrecompiler statements;

	public MySqlGraph(String graphName, MySqlConnectionParameters parameters) throws SQLException {
		
		this.graphName = graphName;

		// Opens a connection to a mySql database from parameters.
		try {
			Class.forName("com.mysql.jdbc.Driver");
			mySql = DriverManager.getConnection(parameters.getUrl(),parameters.getUser(),parameters.getPassword());
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		statements = new MySqlStatementPrecompiler(this);
		
		// Creates the tables for representing the graph.
		Statement st = mySql.createStatement();
		st.executeUpdate("CREATE TABLE IF NOT EXISTS"  + getNodesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id))");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getNodesPropertiesTableName() + 
				" (id BIGINT NOT NULL, PRIMARY KEY(id), p_key" + MySqlUtil.MYSQL_STRING + "NOT NULL, p_value" + MySqlUtil.MYSQL_STRING + "NOT NULL)");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS" + getEdgesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), start INT, end INT)");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getEdgesPropertiesTableName() + 
				" (id BIGINT NOT NULL, PRIMARY KEY(id), p_key" + MySqlUtil.MYSQL_STRING + "NOT NULL, p_value" + MySqlUtil.MYSQL_STRING + "NOT NULL)");

	}

	@Override
	public IVertex createVertex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVertex getVertex(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEdge getEdge(long id) {
		// TODO Auto-generated method stub
		return null;
	}sql

	@Override
	public Iterable<IVertex> getVertices() {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public Iterable<IEdge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	protected Connection getMySqlConnection() {
		return mySql;
	}
	
	protected MySqlStatementPrecompiler getStatements() {
		return statements;
	}
	
	private String getName() {
		return graphName;
	}
	
	private String getNodesTableName() {
		return getName() + "_nodes";
	}
	
	private String getNodesPropertiesTableName() {
		return getName() + "_nodes_properties";
	}
	
	private String getEdgesTableName() {
		return getName() + "_edges";
	}
	
	protected String getEdgesPropertiesTableName() {
		return getName() + "_edges_properties";
	}
}
