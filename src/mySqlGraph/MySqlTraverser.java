package mySqlGraph;

import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.ITraverser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import util.DataAccessException;



class MySqlTraverser implements ITraverser<MySqlVertex> {

	public enum TraversalType {
		BFS,
		DFS
	}

	/**
	 * 
	 * Traverses vertices of a mysql graph using BFS,
	 * and specified parameters like depth and edge types.
	 * 
	 * @author iz2
	 *
	 */
	class TraverserIterator implements Iterator<MySqlVertex> {

		private static final int UNVISITED_VERTEX = -1;

		private Connection connection;

		private long currentDepth;
		private PreparedStatement getDepthStatement;

		private PreparedStatement markDepthStatement;
		private long nextDepth;
		private Long nextVertexId;
		private Queue<Long> q;

		/**
		 * 
		 * Creates a new connection for the traversal.
		 * 
		 * @param startId
		 * @throws SQLException
		 */
		protected TraverserIterator(long startId) throws SQLException {

			connection = graph.getConnector().connect();
			createDepthTable();
			
			// Adds the start vertex to the bfs traversal queue.
			q = new LinkedList<Long>();
			q.add(startId);
			executeMarkDepth(startId, 0);

			// Traverses all vertices with depth smaller than minimal depth.
			nextVertexId = null;
			currentDepth = -1;
			while (q.isEmpty() == false && nextVertexId == null) {
				traverse();
			}
		}

		private void createDepthTable() throws SQLException {
			Statement statement = connection.createStatement();
			try {


				// Creates the depth table, and prepares statements for it.
				statement.executeUpdate("CREATE TEMPORARY TABLE depth_table (id BIGINT NOT NULL, depth BIGINT NOT NULL, PRIMARY KEY(id))");
				markDepthStatement = connection.prepareStatement("INSERT INTO depth_table (id, depth) VALUES(?, ?)");
				getDepthStatement = connection.prepareStatement("SELECT depth FROM depth_table WHERE id = ?" );


			} finally {
				statement.close();
			}
		}

		private void executeMarkDepth(long node, long depth) throws SQLException {
			markDepthStatement.setLong(1, node);
			markDepthStatement.setLong(2, depth);
			markDepthStatement.executeUpdate();
		}

		public long getDepth() {
			return currentDepth;
		}

		private long getDepth(long vertexId) throws SQLException {

			// Executes the get depth statement.
			getDepthStatement.setLong(1, vertexId);
			ResultSet rs = getDepthStatement.executeQuery();
			try {


				// If has depth,
				if (rs.next()) {

					// returns it.
					return rs.getLong(1);
				}

				// Otherwise identifies that the vertex is unvisited.
				return UNVISITED_VERTEX;


			} finally {
				rs.close();
			}
		}

		@Override
		public boolean hasNext() {
			return nextVertexId != null;
		}
		
		@Override
		public MySqlVertex next() {
			try {

				if (nextVertexId == null) {
					throw new NoSuchElementException("No more vertices to traverse.");
				}

				long firstTmp = nextVertexId;
				
				// Gets the vertex to return next time.
				nextVertexId = null;
				if (q.isEmpty() == false) {
					traverse();
				}

				return new MySqlVertex(firstTmp, graph);

			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * 
		 * @return - id of traversed vertex.
		 * @throws SQLException 
		 */
		private void traverse() throws SQLException {

			// gets the next vertex.
			long vertexId = q.poll();

			// Gets its depth, and if the depth is not larger than maximum depth, then
			long nodeDepth = getDepth(vertexId);
			if (nodeDepth <= maxDepth) {

				if (nodeDepth >= minDepth) {
					nextVertexId = vertexId;
					currentDepth = nextDepth;
					nextDepth = nodeDepth;
				}

				if (nodeDepth < maxDepth) {

					// gets all child vertices and visits them.
					switch (allowedDirection) {
					case BOTH : {

						// Gets child/parent vertices.
						ResultSet outgoing = executeGetOutgoingEdges(vertexId);
						visitChildNodes(q, outgoing, nodeDepth);

						ResultSet ingoing = executeGetIncomingEdges(vertexId);
						visitChildNodes(q, ingoing, nodeDepth);
					}
					case INCOMING : {

						// Gets only parent vertices.
						ResultSet ingoing = executeGetIncomingEdges(vertexId);
						visitChildNodes(q, ingoing, nodeDepth);
						break;
					}
					case OUTGOING : {

						// Gets only child vertices.
						ResultSet outgoing = executeGetOutgoingEdges(vertexId);
						visitChildNodes(q, outgoing, nodeDepth);
						break;
					}

					default:
						// Shouldn't happen.
						throw new IllegalArgumentException("Illegal value of allowed direction enum : " + allowedDirection);
					}
				}
			}
		}

		private void visitChildNodes(Queue<Long> q, ResultSet resultSet, long nodeDepth) throws SQLException {

			while (resultSet.next()) {

				// Gets the child id, its depth and edge type.
				long child = resultSet.getLong(1);
				long childDepth = getDepth(child);

				// If it hasn't been visited yet.
				if (childDepth == UNVISITED_VERTEX) {

					// Gets the edge type.
					String childType = resultSet.getString(2);

					// Checks if edge type is include (if allowed edge types are empty, all types are included).
					boolean include = true;
					if (allowedEdgeTypes.isEmpty() == false && allowedEdgeTypes.contains(childType) == false) {
						include = false;
					}

					// If included,
					if (include) {

						// then adds the child to the queue and marks its depth (one larger than the parent's depth).
						q.add(child);
						executeMarkDepth(child, nodeDepth + 1);
					}
				}
			}
		}
	}
	private Direction allowedDirection;
	private List<String> allowedEdgeTypes;
	private PreparedStatement getIncomingEdges;
	private PreparedStatement getOutgoingEdges;

	private MySqlGraph graph;
	private int maxDepth;

	private int minDepth;

	protected MySqlTraverser(MySqlGraph graph, int minDepth, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) throws SQLException {

		this.graph = graph;
		this.minDepth = minDepth;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;

		getOutgoingEdges = graph.getMySqlConnection().prepareStatement("SELECT end, type FROM " + graph.getEdgesTableName() + " WHERE start = ?");
		getIncomingEdges = graph.getMySqlConnection().prepareStatement("SELECT start, type FROM " + graph.getEdgesTableName() + " WHERE end = ?");
	}

	/**
	 * 
	 * @param endId
	 * 
	 * @return a result set containing all incoming edges.
	 * The first column is the id of the other vertex, and the second is the type of the edge.
	 * 
	 * @throws SQLException
	 */
	private ResultSet executeGetIncomingEdges(long endId) throws SQLException {
		getIncomingEdges.setLong(1, endId);
		return getIncomingEdges.executeQuery();
	}

	/**
	 * 
	 * 
	 * 
	 * @param startId
	 * 
	 * @return a result set containing all outgoing edges.
	 * The first column is the id of the other vertex, and the second is the type of the edge.
	 * 
	 * @throws SQLException
	 * 
	 */
	private ResultSet executeGetOutgoingEdges(long startId) throws SQLException {
		getOutgoingEdges.setLong(1, startId);
		return getOutgoingEdges.executeQuery();
	}

	/**
	 * 
	 * Each new iterator opens a new connection, so that temporary tables
	 * can be used for marking if node was visited or not (depth).
	 * 
	 * @param vertex
	 * @return
	 */
	@Override
	public Iterable<MySqlVertex> traverse(final MySqlVertex vertex) {

		return new Iterable<MySqlVertex>() {

			@Override
			public Iterator<MySqlVertex> iterator() {
				try {


					return new TraverserIterator(vertex.getId());


				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}

		};
	}
}
