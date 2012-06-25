package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

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

class MySqlTraverser {

	public enum TraversalType {
		BFS,
		DFS
	}

	private MySqlGraph graph;
	private int maxDepth;
	private List<String> allowedEdgeTypes;
	private Direction allowedDirection;

	private PreparedStatement getOutgoingEdges;
	private PreparedStatement getIngoingEdges;

	// TODO : vertices from the same graph.

	protected MySqlTraverser(MySqlGraph graph, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) throws SQLException {

		this.graph = graph;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;

		getOutgoingEdges = graph.getMySqlConnection().prepareStatement("SELECT end, type FROM " + graph.getEdgesTableName() + " WHERE start = ?");
		getIngoingEdges = graph.getMySqlConnection().prepareStatement("SELECT start, type FROM " + graph.getEdgesTableName() + " WHERE end = ?");
	}



	/**
	 * 
	 * Each new iterator opens a new connection, so that temporary tables
	 * can be used for marking if node was visited or not (depth).
	 * 
	 * @param vertex
	 * @return
	 */
	public Iterable<MySqlVertex> traverse(final IVertex vertex) {

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

	private class TraverserIterator implements Iterator<MySqlVertex> {
		
		private static final int UNVISITED_VERTEX = -1;
		
		private Connection connection;
		
		private PreparedStatement markDepthStatement;
		private PreparedStatement getDepthStatement;
		
		// TODO : resource cleanup.

		private Queue<Long> q;

		private TraverserIterator(long startId) throws SQLException {
			
			// Creates the new connection for the traversal.
			connection = graph.getConnector().connect();
			
			// Sets up the depth table.
			createDepthTable();
			
			// Adds the start vertex to the bfs traversal queue.
			q = new LinkedList<Long>();
			q.add(startId);
			executeMarkDepth(startId, 0);
		}
		
		private void createDepthTable() throws SQLException {
			Statement statement = connection.createStatement();
			try {
				
				
				// Creates the depth table, and prepares statements for it.
				statement.executeUpdate("CREATE TABLE #depth_table (id BIGINT NOT NULL, depth BIGINT NOT NULL, PRIMARY KEY(id))");
				markDepthStatement = connection.prepareStatement("INSERT INTO #depth_table (id, depth) VALUES(?, ?)");
				getDepthStatement = connection.prepareStatement("SELECT depth FROM #depth_table WHERE id = ?" );
				
				
			} finally {
				statement.close();
			}
		}

		@Override
		public boolean hasNext() {
			return q.isEmpty() == false;
		}

		@Override
		public MySqlVertex next() {
			try {


				// If there are more vertices to traverse,
				if (q.isEmpty() == false) {

					// gets the next vertex.
					long vertexId = q.poll();
					MySqlVertex nextVertex = new MySqlVertex(vertexId, graph);

					// Gets its depth, and if the depth is not larger than maximum depth, then
					long nodeDepth = getDepth(vertexId);
					if (nodeDepth <= maxDepth) {

						// gets all child vertices and visits them.
						switch (allowedDirection) {
						case BOTH : {

							// Gets child/parent vertices.
							ResultSet outgoing = executeGetOutgoingEdges(vertexId);
							visitChildNodes(q, outgoing, nodeDepth);

							ResultSet ingoing = executeGetIngoingEdges(vertexId);
							visitChildNodes(q, ingoing, nodeDepth);
						}
						case INCOMING : {

							// Gets only parent vertices.
							ResultSet ingoing = executeGetIngoingEdges(vertexId);
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
					
					return nextVertex;
				}
				else {
					throw new NoSuchElementException("No more vertices to traverse.");
				}
			} catch (SQLException e) {
				throw new DataAccessException(e);
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
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
		
		private void executeMarkDepth(long node, long depth) throws SQLException {
			markDepthStatement.setLong(1, depth);
			markDepthStatement.setLong(2, node);
			markDepthStatement.executeUpdate();
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
	}
	
	private ResultSet executeGetOutgoingEdges(long startId) throws SQLException {
		getOutgoingEdges.setLong(1, startId);
		return getOutgoingEdges.executeQuery();
	}

	private ResultSet executeGetIngoingEdges(long endId) throws SQLException {
		getIngoingEdges.setLong(1, endId);
		return getIngoingEdges.executeQuery();
	}
}
