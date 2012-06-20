package mySqlGraph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import exceptions.DataAccessException;

import graphInterfaces.IIndex;
import graphInterfaces.IIndexManager;

public class MySqlIndexManager implements IIndexManager<MySqlVertex, MySqlEdge> {

	private MySqlGraph graph;

	private PreparedStatement addVertexIndex;

	protected MySqlIndexManager(MySqlGraph graph) throws SQLException {

		this.graph = graph;

		Statement st = graph.getMySqlConnection().createStatement();
		try {

			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + getVertexIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ")");

		} finally {
			st.close();
		}


		addVertexIndex = graph.getMySqlConnection().prepareStatement("INSERT IGNORE INTO " + graph.getName() + "_vertex_indexes (name) VALUES(?)");
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
		// TODO Auto-generated method stub
		return null;
	}

	protected void clear() throws SQLException {
		Statement st = null;
		ResultSet rs = null;
		try {

			
			st = graph.getMySqlConnection().createStatement();
			rs = st.executeQuery("SELECT name FROM " + getVertexIndexNamesTable());

			ArrayList<String> tablesToDrop = new ArrayList<String>();
			while (rs.next()) {
				tablesToDrop.add(graph.getName() + "_vertices_" + rs.getString(1));
			}
			
			for (String tableToDrop : tablesToDrop) {
				st.executeUpdate("DROP TABLE " + tableToDrop);
			}

			// Recreates the names of vertex indexes table.
			st.executeUpdate("DROP TABLE " + getVertexIndexNamesTable());
			st.executeUpdate("CREATE TABLE " + getVertexIndexNamesTable() + " (name " + MySqlGraph.MYSQL_STRING + ")");

			
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { /* IGNORE */}
			if (st != null) try { st.close(); } catch (SQLException e) { /* IGNORE */}
		}
	}

	private void executeAddVertexIndex(String name) throws SQLException {
		addVertexIndex.setString(1, name);
		addVertexIndex.executeUpdate();
	}

	private String getVertexIndexNamesTable() {
		return graph.getName() + "_vertex_indexes";
	}
}
