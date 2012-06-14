package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class MySqlStatementPrecompiler {
	
	private PreparedStatement createVertex;
	private PreparedStatement createEdge;
	
	private PreparedStatement getVertexProperty;
	private PreparedStatement setVertexProperty;
	
	private PreparedStatement getEdgeProperty;
	private PreparedStatement setEdgeProperty;
	
	// TODO : remove
	private PreparedStatement getEdgeStartId;
	private PreparedStatement getEdgeEndId;
	
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
		
		getEdgeStartId = graph.getMySqlConnection().prepareStatement("SELECT start FROM " + graph.getEdgesTableName() + " WHERE id = ?");
		getEdgeEndId = graph.getMySqlConnection().prepareStatement("SELECT end FROM " + graph.getEdgesTableName() + " WHERE id = ?");
		
		getVertex = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName() + " WHERE id = ?");
		getEdge = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName() + " WHERE id = ?");
		
		getAllVertices = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getNodesTableName());
		getAllEdges = graph.getMySqlConnection().prepareStatement("SELECT * FROM " + graph.getEdgesTableName());
	}
	
	protected PreparedStatement getCreateVertex() {
		return createVertex;
	}
	
	protected PreparedStatement getCreateEdge() {
		return createEdge;
	}

	protected PreparedStatement getGetVertexProperty() {
		return getVertexProperty;
	}

	protected PreparedStatement getSetVertexProperty() {
		return setVertexProperty;
	}

	protected PreparedStatement getGetEdgeProperty() {
		return getEdgeProperty;
	}

	protected PreparedStatement getSetEdgeProperty() {
		return setEdgeProperty;
	}

	protected PreparedStatement getGetEdgeStartId() {
		return getEdgeStartId;
	}

	protected PreparedStatement getGetEdgeEndId() {
		return getEdgeEndId;
	}

	protected PreparedStatement getGetVertex() {
		return getVertex;
	}
	
	protected PreparedStatement getGetEdge() {
		return getEdge;
	}

	protected PreparedStatement getGetAllVertices() {
		return getAllVertices;
	}

	protected PreparedStatement getGetAllEdges() {
		return getAllEdges;
	}
}
