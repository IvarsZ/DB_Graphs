package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IIndexManager;
import graphInterfaces.IPersistentGraph;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;




/**
 * 
 * MySql implementation of the graph interface.
 * 
 * @author iz2
 *
 */
public class MySqlGraph implements IPersistentGraph<MySqlVertex, MySqlEdge> {

	protected static final String MYSQL_STRING = " VARCHAR(50) ";

	private String graphName;

	private Connection mySql;

	private GraphStatements statements;
	
	private MySqlIndexManager index;

	public MySqlGraph(String graphName, MySqlConnector connector) throws SQLException {

		this.graphName = graphName;

		// Connects to the database, and cancels auto commit.
		mySql = connector.connect();
		mySql.setAutoCommit(false);

		statements = new GraphStatements(this);
		index = new MySqlIndexManager(this);

		// Creates the tables for representing the graph.
		createTables();
	}

	@Override
	public MySqlVertex createVertex() throws DataAccessException {
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
	public MySqlEdge createEdge(MySqlVertex start, MySqlVertex end)
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
	public MySqlVertex getVertex(long id) throws DataAccessException {
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
	public MySqlEdge getEdge(long id) throws DataAccessException {
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
	public Iterable<MySqlVertex> getVertices() {
		return new Iterable<MySqlVertex>() {
			@Override
			public Iterator<MySqlVertex> iterator() {
				try {
					

					// Returns the vertex iterator.
					return new MySqlVertexIterator(statements.executeGetAllVertices(), MySqlGraph.this);

					
				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}
		};
	}

	@Override
	public Iterable<MySqlEdge> getEdges() {
		return new Iterable<MySqlEdge>() {
			@Override
			public Iterator<MySqlEdge> iterator() {
				try {

					// Returns new edge iterator.
					return new MySqlEdgeIterator(statements.executeGetAllEdges(), MySqlGraph.this);


				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}

		};
	}
	
	@Override
	public IGraphOperator<MySqlVertex, MySqlEdge> getOperator() {
		try {
			
			
			return new MySqlGraphOperator(this);
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	@Override
	public IIndexManager<MySqlVertex, MySqlEdge> index() {
		return index;
	}

	@Override
	public void clear() throws DataAccessException {
		try {


			// Recreates the tables.
			dropTables();
			createTables();
			
			// Deletes indexes.
			index.clear();


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void close() throws DataAccessException {
		try {

			// Rolls back and closes the connection.
			mySql.rollback();
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

	protected GraphStatements getStatements() {
		return statements;
	}

	protected String getName() {
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

	private boolean belongToGraph(MySqlVertex vertex) throws DataAccessException {
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
