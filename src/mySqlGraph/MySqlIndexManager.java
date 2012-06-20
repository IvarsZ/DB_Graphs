package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import exceptions.DataAccessException;

import graphInterfaces.IIndex;
import graphInterfaces.IIndexManager;

/**
 * 
 * MySql implementation of the index manager interface.
 * 
 * @author iz2
 *
 */
public class MySqlIndexManager implements IIndexManager<MySqlVertex, MySqlEdge> {

	private MySqlGraph graph;

	private PreparedStatement addVertexIndex;
	private PreparedStatement addEdgeIndex;

	protected MySqlIndexManager(MySqlGraph graph) throws SQLException {

		this.graph = graph;

		Statement st = graph.getMySqlConnection().createStatement();
		try {

			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + getVertexIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ", PRIMARY KEY(name))");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + getEdgeIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ", PRIMARY KEY(name))");

		} finally {
			st.close();
		}


		addVertexIndex = graph.getMySqlConnection().prepareStatement("INSERT IGNORE INTO " + getVertexIndexNamesTable() + " (name) VALUES(?)");
		addEdgeIndex = graph.getMySqlConnection().prepareStatement("INSERT IGNORE INTO " + getEdgeIndexNamesTable() + " (name) VALUES(?)");
	}

	@Override
	public IIndex<MySqlVertex> forVertices(String indexName) {


		indexName = indexName.replaceAll("\\s","");
		String tableName = graph.getName() + "_vertices_" + indexName;
		try {


			// Creates the new index and adds its name to vertex indexes.
			executeAddVertexIndex(indexName);
			return new MySqlVertexIndex(tableName, graph);


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public IIndex<MySqlEdge> forEdges(String indexName) {
		
		
		indexName = indexName.replaceAll("\\s","");
		String tableName = graph.getName() + "_edges_" + indexName;
		try {


			// Creates the new index and adds its name to vertex indexes.
			executeAddEdgeIndex(indexName);
			return new MySqlEdgeIndex(tableName, graph);


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	protected void clear() throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		try {

			
			// Drops the vertex and edge index tables.
			st = graph.getMySqlConnection().createStatement();
			rs = st.executeQuery("SELECT name FROM " + getVertexIndexNamesTable());
			dropTables(rs, st, "_vertices_");
			rs = st.executeQuery("SELECT name FROM " + getEdgeIndexNamesTable());
			dropTables(rs, st, "_edges_");

			// Recreates the names of vertex and edge indexes tables.
			st.executeUpdate("DROP TABLE " + getVertexIndexNamesTable());
			st.executeUpdate("CREATE TABLE " + getVertexIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ")");
			st.executeUpdate("DROP TABLE " + getEdgeIndexNamesTable());
			st.executeUpdate("CREATE TABLE " + getEdgeIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ")");

			
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { /* IGNORE */}
			if (st != null) try { st.close(); } catch (SQLException e) { /* IGNORE */}
		}
	}

	private void executeAddVertexIndex(String name) throws SQLException {
		addVertexIndex.setString(1, name);
		addVertexIndex.executeUpdate();
	}
	
	private void executeAddEdgeIndex(String name) throws SQLException {
		addEdgeIndex.setString(1, name);
		addEdgeIndex.executeUpdate();
	}

	private String getVertexIndexNamesTable() {
		return graph.getName() + "_vertex_indexes";
	}
	
	private String getEdgeIndexNamesTable() {
		return graph.getName() + "_edge_indexes";
	}
	
	private void dropTables(ResultSet rs, Statement st, String entity) throws SQLException {
		
		ArrayList<String> tablesToDrop = new ArrayList<String>();
		while (rs.next()) {
			tablesToDrop.add(graph.getName() + entity + rs.getString(1));
		}
		
		for (String tableToDrop : tablesToDrop) {
			st.executeUpdate("DROP TABLE " + tableToDrop);
		}
	}
}
