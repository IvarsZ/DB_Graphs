package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySqlStatementPrecompiler {

	private PreparedStatement createVertex;
	private PreparedStatement createEdge;

	private PreparedStatement getVertexProperty;
	private PreparedStatement setVertexProperty;

	private PreparedStatement getEdgeProperty;
	private PreparedStatement setEdgeProperty;

	private PreparedStatement getVertex;
	private PreparedStatement getEdge;

	private PreparedStatement getAllVertices;
	private PreparedStatement getAllEdges;

	protected MySqlStatementPrecompiler(MySqlGraph graph) throws SQLException {

		createVertex = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getNodesTableName() + " (id) VALUES (DEFAULT)", Statement.RETURN_GENERATED_KEYS);
		createEdge = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getEdgesTableName() + " (id, start, end) VALUES (DEFAULT, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		getVertexProperty = graph.getMySqlConnection().prepareStatement("SELECT p_value FROM " + graph.getNodesPropertiesTableName() + " WHERE id = ?, p_key = ?");
		setVertexProperty = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getNodesPropertiesTableName() + " (id, p_key, p_value) VALUES(?, ?, ?)");

		getEdgeProperty = graph.getMySqlConnection().prepareStatement("SELECT p_value FROM " + graph.getEdgesPropertiesTableName() + " WHERE id = ?, p_key = ?");
		setEdgeProperty = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getEdgesPropertiesTableName() + " (id, p_key, p_value) VALUES(?, ?, ?)");

		getVertex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName() + " WHERE id = ?");
		getEdge = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName() + " WHERE id = ?");

		getAllVertices = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName());
		getAllEdges = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName());
	}

	/**
	 * 
	 * Closes all precompiled statements.
	 * 
	 * @throws SQLException
	 */
	protected void close() throws SQLException {

		// TODO : not all could be closed.
		createVertex.close();
		createEdge.close();

		getVertexProperty.close();
		setVertexProperty.close();

		getEdgeProperty.close();
		setEdgeProperty.close();

		getVertex.close();
		getEdge.close();

		getAllVertices.close();
		getAllEdges.close();
	}
	
	protected ResultSet executeCreateVertex() throws SQLException {
		
		// Executes the create vertex statement and returns result set with inserted id's.
		createVertex.executeUpdate();
		
		return createVertex.getGeneratedKeys();
	}
	
	protected ResultSet executeCreateEdge(long startId, long endId) throws SQLException {
		
		// Sets start and end ids, executes the create edge, and returns inserted id's.
		createEdge.setLong(1, startId);
		createEdge.setLong(2, endId);
		createEdge.executeUpdate();
		
		return createEdge.getGeneratedKeys();
	}
	
	protected ResultSet executeGetVertex(long vertexId) throws SQLException {
		
		getVertex.setLong(1, vertexId);
		return getVertex.executeQuery();
	}
	
	protected ResultSet executeGetEdge(long edgeId) throws SQLException {
		
		getEdge.setLong(1, edgeId);
		return getEdge.executeQuery();
	}

	protected ResultSet executeGetVertexProperty(long id, String key) throws SQLException {
		getVertexProperty.setLong(1, id);
		getVertexProperty.setString(2, key);
		return getVertexProperty.executeQuery();
	}

	protected void executeSetVertexProperty(long id, String key, String value) throws SQLException {
		setVertexProperty.setLong(1, id);
		setVertexProperty.setString(2, key);
		setVertexProperty.setString(3, value);
		setVertexProperty.executeUpdate();
	}

	protected ResultSet executeGetEdgeProperty(long id, String key) throws SQLException {
		getEdgeProperty.setLong(1, id);
		getEdgeProperty.setString(2, key);
		return getEdgeProperty.executeQuery();
	}

	protected void executeSetEdgeProperty(long id, String key, String value) throws SQLException {
		
		// Sets id, key and value, then executes.
		setEdgeProperty.setLong(1, id);
		setEdgeProperty.setString(2, key);
		setEdgeProperty.setString(3, value);
		setEdgeProperty.executeUpdate();
	}

	protected ResultSet executeGetAllVertices() throws SQLException {
		return getAllVertices.executeQuery();
	}

	protected ResultSet executeGetAllEdges() throws SQLException {
		return getAllEdges.executeQuery();
	}
}
