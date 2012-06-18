package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MySqlGraph implements IPersistentGraph {

	private static final String MYSQL_STRING = " VARCHAR(50) ";

	private String graphName;

	private Connection mySql;

	private MySqlStatementPrecompiler statements;

	public MySqlGraph(String graphName, MySqlConnector connector) throws SQLException {

		this.graphName = graphName;

		// Connects to the database, and cancels auto commit.
		mySql = connector.connect();
		mySql.setAutoCommit(false);

		statements = new MySqlStatementPrecompiler(this);

		// Creates the tables for representing the graph.
		createTables();
	}

	@Override
	public IVertex createVertex() throws DataAccessException {
		ResultSet rs = null;
		try {


			// Creates the vertex in MySql database and gets its id.
			rs = getStatements().executeCreateVertex();
			rs.next();
			long id = rs.getInt(1);

			return new MySqlVertex(id, this);


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		finally {
			if (rs!= null) try { rs.close(); } catch (SQLException e) {/* Ignore */};
		}
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException, DataAccessException {
		ResultSet rs = null;
		try {

			// If both start and end vertices belong to the graph
			if (belongToGraph(start) && belongToGraph(end)) {


				// Creates the edge in the MySql database and gets its id.
				rs = getStatements().executeCreateEdge(start.getId(), end.getId());
				rs.next();
				long id = rs.getInt(1);
				return new MySqlEdge(id, (MySqlVertex) start, (MySqlVertex) end, this);
			}

			// Otherwise throw IllegalArgumentException.
			else {
				throw new IllegalArgumentException("Vertex " + start + " or " + end + " doesn't belong the the graph");
			}


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
		finally {
			if (rs!= null) try { rs.close(); } catch (SQLException e) {/* Ignore */};
		}

	}

	@Override
	public IVertex getVertex(long id) throws DataAccessException {
		try {


			// If there is a vertex with the id in the graph.
			if (containsVertex(id)) {

				// creates and returns it.
				return new MySqlVertex(id, this);
			}
			else {

				// Otherwise returns null.
				return null;
			}


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public IEdge getEdge(long id) throws DataAccessException {
		ResultSet rs = null;
		try {


			rs = getStatements().executeGetEdge(id);
			if (rs.next()) {

				// Creates and returns the MySqlEdge.
				return new MySqlEdge(id, (MySqlVertex) getVertex(rs.getLong(2)), (MySqlVertex) getVertex(rs.getLong(3)), this);
			}

			// No edge row had the id, so there is no edge with the id.
			else {
				return null;
			}


		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			try { if (rs != null) rs.close();} catch (SQLException e) { /* Ignored */ }
		}
	}

	@Override
	public Iterable<IVertex> getVertices() {
		return new Iterable<IVertex>() {
			@Override
			public Iterator<IVertex> iterator() {
				try {


					// Returns the vertex iterator.
					return new VertexIterator();


				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}
		};
	}

	private class VertexIterator implements Iterator<IVertex> {

		// TODO : resource closure.

		ResultSet vertexIterator;

		public VertexIterator() throws SQLException {

			// Query all vertices.
			vertexIterator = getStatements().executeGetAllVertices();

		}

		@Override
		public boolean hasNext() {
			try {


				boolean hasNext = vertexIterator.next();
				if(hasNext) {

					// Reset the cursor back to its previous position.
					vertexIterator.previous();
				}

				return hasNext;


			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		@Override
		public IVertex next() {
			try {

				// If there is a next vertex creates and returns it.
				if (vertexIterator.next()) {
					return new MySqlVertex(vertexIterator.getLong(1), MySqlGraph.this);
				}

				// Otherwise throws NoSuchElementException.
				else {
					throw new NoSuchElementException("Vertex Iterator " + vertexIterator + " has no more vertices.");
				}

			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		@Override
		public void remove() {

			// TODO : Implement?
			throw new UnsupportedOperationException("Removal not supported");
		}

	}

	@Override
	public Iterable<IEdge> getEdges() {
		return new Iterable<IEdge>() {
			@Override
			public Iterator<IEdge> iterator() {
				try {

					// Returns new edge iterator.
					return new EdgeIterator();


				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}

		};
	}

	private class EdgeIterator implements Iterator<IEdge> {

		// TODO : resource closure.

		ResultSet edgeIterator;

		public EdgeIterator() throws SQLException {

			// Query all edges.
			edgeIterator = getStatements().executeGetAllEdges();

		}

		@Override
		public boolean hasNext() {
			try {


				boolean hasNext = edgeIterator.next();
				if(hasNext) {

					// Reset the cursor back to its previous position
					edgeIterator.previous();
				}

				return hasNext;


			} catch (SQLException e) {
				throw new DataAccessException(e);
			}			
		}

		@Override
		public IEdge next() {
			try {


				// If there is a next edge creates and returns it.
				if (edgeIterator.next()) {
					return new MySqlEdge(edgeIterator.getLong(1), (MySqlVertex) getVertex(edgeIterator.getLong(2)), (MySqlVertex) getVertex(edgeIterator.getLong(3)), MySqlGraph.this);
				}

				// Otherwise throws NoSuchElementException.
				else {
					throw new NoSuchElementException("Edge Iterator " + edgeIterator + " has no more edges.");
				}


			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		@Override
		public void remove() {

			// TODO : Implement?
			throw new UnsupportedOperationException("Removal not supported");
		}
	}

	@Override
	public void clear() throws DataAccessException {
		try {


			// Recreates the tables.
			dropTables();
			createTables();


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void close() throws DataAccessException {
		try {

			// Rolls back and closes all statements and the connection.
			mySql.rollback();
			statements.close();
			mySql.close();


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void commit() throws DataAccessException {
		try {


			mySql.commit();


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void rollback() throws DataAccessException {
		try {


			mySql.rollback();


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
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

	protected String getNodesTableName() {
		return getName() + "_nodes";
	}

	protected String getNodesPropertiesTableName() {
		return getName() + "_nodes_properties";
	}

	protected String getEdgesTableName() {
		return getName() + "_edges";
	}

	protected String getEdgesPropertiesTableName() {
		return getName() + "_edges_properties";
	}

	private void createTables() throws SQLException {
		Statement st = mySql.createStatement();
		try {


			// Creates tables for vertices, edges an their properties.
			st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getNodesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id))");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getNodesPropertiesTableName() + 
					" (id BIGINT NOT NULL, p_key" + MYSQL_STRING + "NOT NULL, p_value" + MYSQL_STRING + "NOT NULL)");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS " + getEdgesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), start BIGINT NOT NULL, end BIGINT NOT NULL)");
			st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getEdgesPropertiesTableName() + 
					" (id BIGINT NOT NULL, p_key" + MYSQL_STRING + "NOT NULL, p_value" + MYSQL_STRING + "NOT NULL)");


		} finally {
			st.close();
		}	
	}

	private void dropTables() throws SQLException {
		Statement st = mySql.createStatement();
		try {


			// Drops all tables.
			st.executeUpdate("DROP TABLE " + getNodesTableName());
			st.executeUpdate("DROP TABLE " + getNodesPropertiesTableName());
			st.executeUpdate("DROP TABLE " + getEdgesTableName());
			st.executeUpdate("DROP TABLE " + getEdgesPropertiesTableName());


		} finally {
			st.close();
		}
	}

	private boolean containsVertex(long id) throws SQLException {
		ResultSet rs = getStatements().executeGetVertex(id);
		try {

			// Returns whether there are rows with the vertex id.
			return rs.next();

		}
		finally {
			rs.close();
		}
	}

	private boolean belongToGraph(IVertex vertex) throws DataAccessException {
		try {

			// If it is MySql vertex, 
			if (vertex instanceof MySqlVertex) {

				// and matches the vertex in the database with its id, then belongs to the graph.
				return vertex.equals(getVertex(vertex.getId()));
			}

			return false;
		} catch (DataAccessException e) {
			throw e;
		}
	}
}
