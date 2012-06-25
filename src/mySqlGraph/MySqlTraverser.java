package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

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

	// TODO : unique column.
	private static final String MARK_DEPTH_COLUMN = "depth_in_findNeighbours";

	public enum TraversalType {
		BFS,
		DFS
	}

	private MySqlGraph graph;
	private TraversalType traversalType;
	private int maxDepth;
	private List<String> allowedEdgeTypes;
	private Direction allowedDirection;

	private PreparedStatement getOutgoingEdges;
	private PreparedStatement getIngoingEdges;

	private PreparedStatement markDepthStatement;
	private PreparedStatement getDepthStatement;

	// TODO : vertices from the same graph.

	protected MySqlTraverser(MySqlGraph graph, TraversalType traversalType, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) throws SQLException {

		this.graph = graph;
		this.traversalType = traversalType;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;

		getOutgoingEdges = graph.getMySqlConnection().prepareStatement("SELECT end FROM " + graph.getEdgesTableName() + " WHERE start = ?");
		getIngoingEdges = graph.getMySqlConnection().prepareStatement("SELECT start FROM " + graph.getEdgesTableName() + " WHERE end = ?");

		markDepthStatement = graph.getMySqlConnection().prepareStatement("UPDATE " + graph.getNodesTableName() + " SET " + MARK_DEPTH_COLUMN + " = ? WHERE id = ? ;");
		getDepthStatement = graph.getMySqlConnection().prepareStatement("SELECT " + MARK_DEPTH_COLUMN + " FROM "  + graph.getNodesTableName() + " WHERE id = ? ;" );
	}



	private Iterable<MySqlVertex> traverse(final IVertex vertex) {

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

		// TODO : cleanup.

		private Queue<Long> q;

		private TraverserIterator(long startId) throws SQLException {

			createMarkDepthColumn();

			q = new LinkedList<Long>();
			q.add(startId);
			executeMarkDepth(startId, 1);	
		}

		@Override
		public boolean hasNext() {
			return q.isEmpty() == false;
		}

		@Override
		public MySqlVertex next() {
			try {


				if (q.isEmpty() == false) {

					long node = q.poll();
					MySqlVertex nextVertex = new MySqlVertex(node, graph);

					// Gets the depth, and adds the node to neighbours if required (note nodeDepth is larger by one than actual depth).
					long nodeDepth = getDepth(node);

					// Gets all child vertices and adds them if appropriate to the queue.
					switch (allowedDirection) {
					case BOTH : {

						// Gets child/parent vertices.
						ResultSet outgoing = executeGetOutgoingEdges(node);
						visitChildNodes(q, outgoing, nodeDepth);

						ResultSet ingoing = executeGetIngoingEdges(node);
						visitChildNodes(q, ingoing, nodeDepth);
					}
					case INCOMING : {

						// Gets only parent vertices.
						ResultSet ingoing = executeGetIngoingEdges(node);
						visitChildNodes(q, ingoing, nodeDepth);
						break;
					}
					case OUTGOING : {

						// Gets only child vertices.
						ResultSet outgoing = executeGetOutgoingEdges(node);
						visitChildNodes(q, outgoing, nodeDepth);
						break;
					}

					default:
						// Shouldn't happen.
						throw new IllegalArgumentException("Illegal value of allowed direction enum : " + allowedDirection);
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

	}

	private void createMarkDepthColumn() throws SQLException {
		Statement statement = graph.getMySqlConnection().createStatement();
		try {


			statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " ADD " + MARK_DEPTH_COLUMN + " BIGINT ;");	

			
		} catch (SQLException e) {
			throw e;
		} finally {
			statement.close();
		}
	}

	private void deleteMarkDepthColumn() throws SQLException {
		Statement statement = graph.getMySqlConnection().createStatement();
		try {


			statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " DROP " + MARK_DEPTH_COLUMN);


		} catch (SQLException e) {
			throw e;
		} finally {
			statement.close();
		}
	}

	private void visitChildNodes(Queue<Long> q, ResultSet resultSet, long nodeDepth) throws SQLException {

		while (resultSet.next()) {

			// Gets the child id, its depth and edge type.
			long child = resultSet.getLong(1);
			long childDepth = getDepth(child);
			
			// TODO : edge types, traverse all if allowed is empty.

			// If depth is 0 it hasn't been visited yet.
			if (childDepth == 0) {

				// Add to queue and mark depth (one larger than parrent depth).
				q.add(child);
				executeMarkDepth(child, nodeDepth + 1);
			}
		}
	}

	private void executeMarkDepth(long node, long depth) throws SQLException {
		markDepthStatement.setLong(1, depth);
		markDepthStatement.setLong(2, node);
		markDepthStatement.executeUpdate();
	}

	/**
	 * 
	 * @param node
	 * @param getStatement - the first retrieved column has to contain the depth.
	 * 
	 * @return
	 * 
	 * @throws SQLException
	 * 
	 */
	private long getDepth(long node) throws SQLException {

		// Executes the statement.
		getDepthStatement.setLong(1, node);
		ResultSet rs = getDepthStatement.executeQuery();

		try {


			// Only one row.
			rs.next();
			return rs.getLong(1);


		} finally {
			rs.close();
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
