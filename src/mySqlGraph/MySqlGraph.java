package mySqlGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

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
	public IVertex createVertex() {

		try {

			// Creates the vertex in MySql database.
			PreparedStatement createVertex = getStatements().getCreateVertex();
			createVertex.executeUpdate();

			// Gets its id and creates MySqlVertex.
			ResultSet rs = createVertex.getGeneratedKeys();
			if (rs.next()){
				long id = rs.getInt(1);
				return new MySqlVertex(id, this);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException {

		// If both start and end vertices belong to the graph
		if (belongToGraph(start) && belongToGraph(end)) {

			try {
				// Creates the edge in the MySql database.
				PreparedStatement createEdge = getStatements().getCreateEdge();
				createEdge.setLong(1, start.getId());
				createEdge.setLong(2, end.getId());
				createEdge.executeUpdate();


				// Gets its id and creates MySqlEdge.
				ResultSet rs = createEdge.getGeneratedKeys();
				if (rs.next()){
					long id = rs.getInt(1);
					return new MySqlEdge(id, (MySqlVertex) start, (MySqlVertex) end, this);
				}
				rs.close();


			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		}

		throw new IllegalArgumentException("Vertex " + start + " or " + end + " doesn't belong the the graph");
	}

	private boolean belongToGraph(IVertex vertex) {

		// If it is MySql vertex, 
		if (vertex instanceof MySqlVertex) {

			// and matches the vertex in the database with its id, then belongs to the graph.
			return vertex.equals(getVertex(vertex.getId()));
		}

		return false;
	}

	@Override
	public IVertex getVertex(long id) {

		// If there is a vertex with the id in the graph.
		try {
			if (containsVertex(id)) {

				// creates and returns it.
				return new MySqlVertex(id, this);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public IEdge getEdge(long id) {

		try {

			// Gets the edge row with the id.
			PreparedStatement st = getStatements().getGetEdge();
			st.setLong(1, id);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				// Creates the MySqlEdge. // TODO : the start or end could be null.
				return new MySqlEdge(id, (MySqlVertex) getVertex(rs.getLong(2)), (MySqlVertex) getVertex(rs.getLong(3)), this);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Iterable<IVertex> getVertices() {

		return new Iterable<IVertex>() {

			@Override
			public Iterator<IVertex> iterator() {
				return new VertexIterator();
			}

		};
	}

	private class VertexIterator implements Iterator<IVertex> {

		ResultSet vertexIterator;

		public VertexIterator() {

			try {

				// Query all vertices.
				PreparedStatement st = getStatements().getGetAllVertices();
				vertexIterator = st.executeQuery();

			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}

		@Override
		public boolean hasNext() {

			try {

				boolean hasNext = vertexIterator.next();
				if(hasNext) {

					//reset the cursor back to its previous position
					vertexIterator.previous();
				}

				return hasNext;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public IVertex next() {

			try {

				// If there is a next vertex creates and returns it.
				if (vertexIterator.next()) {
					new MySqlVertex(vertexIterator.getLong(1), MySqlGraph.this);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void remove() {

			// Implement?
			throw new UnsupportedOperationException("Removal not supported");
		}

	}

	@Override
	public Iterable<IEdge> getEdges() {

		return new Iterable<IEdge>() {

			@Override
			public Iterator<IEdge> iterator() {
				return new EdgeIterator();
			}

		};
	}

	private class EdgeIterator implements Iterator<IEdge> {

		ResultSet edgeIterator;

		public EdgeIterator() {


			try {

				// Query all edges.
				PreparedStatement st = getStatements().getGetAllEdges();
				edgeIterator = st.executeQuery();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean hasNext() {


			try {

				boolean hasNext = edgeIterator.next();
				if(hasNext) {

					//reset the cursor back to its previous position
					edgeIterator.previous();
				}

				return hasNext;

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}			
		}

		@Override
		public IEdge next() {

			try {

				// If there is a next edge creates and returns it.
				if (edgeIterator.next()) {

					// TODO : the start or end could be null.
					return new MySqlEdge(edgeIterator.getLong(1), (MySqlVertex) getVertex(edgeIterator.getLong(2)), (MySqlVertex) getVertex(edgeIterator.getLong(3)), MySqlGraph.this);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void remove() {
			// Implement?
			throw new UnsupportedOperationException("Removal not supported");
		}
	}

	@Override
	public void clear() {

		// Recreates the tables.
		try {
			dropTables();
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
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
		st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getNodesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id))");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getNodesPropertiesTableName() + 
				" (id BIGINT NOT NULL, PRIMARY KEY(id), p_key" + MYSQL_STRING + "NOT NULL, p_value" + MYSQL_STRING + "NOT NULL)");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS " + getEdgesTableName() + " (id BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY(id), start BIGINT NOT NULL, end BIGINT NOT NULL)");
		st.executeUpdate("CREATE TABLE IF NOT EXISTS "  + getEdgesPropertiesTableName() + 
				" (id BIGINT NOT NULL, PRIMARY KEY(id), p_key" + MYSQL_STRING + "NOT NULL, p_value" + MYSQL_STRING + "NOT NULL)");
		st.close();
		
		// Commits the created tables. TODO : manage exception to roll back if didn't create.
		commit();
	}

	private void dropTables() throws SQLException {

		Statement st = mySql.createStatement();
		st.executeUpdate("DROP TABLE " + getNodesTableName());
		st.executeUpdate("DROP TABLE " + getNodesPropertiesTableName());
		st.executeUpdate("DROP TABLE " + getEdgesTableName());
		st.executeUpdate("DROP TABLE " + getEdgesPropertiesTableName());
		st.close();
		
		// Commits the drop.
		commit();
	}

	private boolean containsVertex(long id) throws SQLException {

		PreparedStatement st = getStatements().getGetVertex();
		st.setLong(1, id);
		ResultSet rs = st.executeQuery();

		// Returns the result and closes the result set.
		boolean contains = rs.next();
		rs.close();
		return contains;
	}
	
	@Override
	public void close() {
		try {
			mySql.rollback();
			statements.close();
			mySql.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void commit() {
		try {
			mySql.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void rollback() {
		try {
			mySql.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
