package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * A class containing prepared statements used in the MySql graph implementation.
 * 
 * @author iz2
 *
 */
class GraphStatements {

	MySqlGraph graph;

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

	protected GraphStatements(MySqlGraph graph) throws SQLException {

		this.graph = graph;		
	}

	protected ResultSet executeCreateVertex() throws SQLException {

		// Prepares statement if unprepared.
		if (createVertex == null) {
			createVertex = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getNodesTableName() + " (id) VALUES (DEFAULT)", Statement.RETURN_GENERATED_KEYS);
		}

		// Executes the create vertex statement and returns result set with inserted id's.
		createVertex.executeUpdate();

		return createVertex.getGeneratedKeys();
	}

	protected ResultSet executeCreateEdge(long startId, long endId, String type) throws SQLException {

		// Prepares statement if unprepared.
		if (createEdge == null) {
			createEdge = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getEdgesTableName() + " (id, start, end, type) VALUES (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		}

		// Sets start and end ids, executes the create edge, and returns inserted id's.
		createEdge.setLong(1, startId);
		createEdge.setLong(2, endId);
		createEdge.setString(3, type);
		createEdge.executeUpdate();

		return createEdge.getGeneratedKeys();
	}

	protected ResultSet executeGetVertex(long vertexId) throws SQLException {

		// Prepares statement if unprepared.
		if (getVertex == null) {
			getVertex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName() + " WHERE id = ?");
		}

		getVertex.setLong(1, vertexId);
		return getVertex.executeQuery();
	}

	protected ResultSet executeGetEdge(long edgeId) throws SQLException {

		// Prepares statement if unprepared.
		if (getEdge == null) {
			getEdge = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName() + " WHERE id = ?");
		}

		getEdge.setLong(1, edgeId);
		return getEdge.executeQuery();
	}

	protected ResultSet executeGetVertexProperty(long id, String key) throws SQLException {

		// Prepares statement if unprepared.
		if (getVertexProperty == null) {
			getVertexProperty = graph.getMySqlConnection().prepareStatement("SELECT p_value FROM " + graph.getNodesPropertiesTableName() + " WHERE id = ? AND p_key = ?");
		}

		getVertexProperty.setLong(1, id);
		getVertexProperty.setString(2, key);
		return getVertexProperty.executeQuery();
	}

	protected void executeSetVertexProperty(long id, String key, String value) throws SQLException {

		// Prepares statement if unprepared.
		if (setVertexProperty == null) {
			setVertexProperty = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getNodesPropertiesTableName() + " (id, p_key, p_value) VALUES(?, ?, ?)");
		}

		setVertexProperty.setLong(1, id);
		setVertexProperty.setString(2, key);
		setVertexProperty.setString(3, value);
		setVertexProperty.executeUpdate();
	}

	protected ResultSet executeGetEdgeProperty(long id, String key) throws SQLException {

		// Prepares statement if unprepared.
		if (getEdgeProperty == null) {
			getEdgeProperty = graph.getMySqlConnection().prepareStatement("SELECT p_value FROM " + graph.getEdgesPropertiesTableName() + " WHERE id = ? AND p_key = ?");
		}

		getEdgeProperty.setLong(1, id);
		getEdgeProperty.setString(2, key);
		return getEdgeProperty.executeQuery();
	}

	protected void executeSetEdgeProperty(long id, String key, String value) throws SQLException {

		// Prepares statement if unprepared.
		if (setEdgeProperty == null) {
			setEdgeProperty = graph.getMySqlConnection().prepareStatement("INSERT INTO " + graph.getEdgesPropertiesTableName() + " (id, p_key, p_value) VALUES(?, ?, ?)");
		}

		// Sets id, key and value, then executes.
		setEdgeProperty.setLong(1, id);
		setEdgeProperty.setString(2, key);
		setEdgeProperty.setString(3, value);
		setEdgeProperty.executeUpdate();
	}

	protected ResultSet executeGetAllVertices() throws SQLException {

		// Prepares statement if unprepared.
		if (getAllVertices == null) {
			getAllVertices = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName());
		}

		return getAllVertices.executeQuery();
	}

	protected ResultSet executeGetAllEdges() throws SQLException {

		// Prepares statement if unprepared.
		if (getAllEdges == null) {
			getAllEdges = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName());
		}

		return getAllEdges.executeQuery();
	}
}
