package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.ITraverser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import mySqlGraph.MySqlTraverser.TraverserIterator;

/**
 * 
 * MySql implementation of the graph operator.
 * 
 * @author iz2
 *
 */
class MySqlGraphOperator implements IGraphOperator<MySqlVertex, MySqlEdge> {

	private static final String MARK_DEPTH_COLUMN = "depth_in_findNeighbours";

	private MySqlGraph graph;

	private PreparedStatement getOutgoingEdges;
	private PreparedStatement getIngoingEdges;

	private PreparedStatement markDepthStatement;
	private PreparedStatement getDepthStatement;

	// TODO : vertices from the same graph.

	protected MySqlGraphOperator(MySqlGraph mySqlGraph) throws SQLException {

		this.graph = (MySqlGraph) mySqlGraph;

		getOutgoingEdges = graph.getMySqlConnection().prepareStatement("SELECT end FROM " + graph.getEdgesTableName() + " WHERE start = ?");
		getIngoingEdges = graph.getMySqlConnection().prepareStatement("SELECT start FROM " + graph.getEdgesTableName() + " WHERE end = ?");

		markDepthStatement = graph.getMySqlConnection().prepareStatement("UPDATE " + graph.getNodesTableName() + " SET " + MARK_DEPTH_COLUMN + " = ? WHERE id = ? ;");
		getDepthStatement = graph.getMySqlConnection().prepareStatement("SELECT " + MARK_DEPTH_COLUMN + " FROM "  + graph.getNodesTableName() + " WHERE id = ? ;" );
	}

	@Override
	public ITraverser<MySqlVertex> createTraverser(int minDepth, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) {
		try {


			return new MySqlTraverser(graph, minDepth, maxDepth, allowedEdgeTypes, allowedDirection);


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public Set<MySqlVertex> findCommonAncestors(MySqlVertex v1, MySqlVertex v2,
			int maxDepth, List<String> allowedEdgeTypes,
			IPersistentGraph.Direction allowedDirection) {

		Set<MySqlVertex> v1Ancestors = new HashSet<MySqlVertex>();

		// Traverses all ancestors of the first node,
		ITraverser<MySqlVertex> traverser = createTraverser(0, maxDepth, allowedEdgeTypes, allowedDirection);
		for (MySqlVertex v : traverser.traverse(v1)) {

			// and adds them to the v1 ancestors set.
			v1Ancestors.add(v);
		}

		Set<MySqlVertex> commonAncestors = new HashSet<MySqlVertex>();

		// For each ancestor of the second node,
		for (MySqlVertex v : traverser.traverse(v2)) {

			// adds it to the common ancestors set if is ancestor of v1, too.
			if (v1Ancestors.contains(v)) {
				commonAncestors.add(v);
			}
		}

		// Returns the set of common ancestors.
		return commonAncestors;
	}

	@Override
	public Set<MySqlVertex> findLowestCommonAncestors(MySqlVertex v1,
			MySqlVertex v2, int maxDepth, List<String> allowedEdgeTypes,
			Direction allowedDirection) {

		// Traverses ancestors of the both nodes
		ITraverser<MySqlVertex> traverser = createTraverser(0, maxDepth, allowedEdgeTypes, allowedDirection);
		TraverserIterator v1Traverser = (TraverserIterator) traverser.traverse(v1).iterator();
		TraverserIterator v2Traverser = (TraverserIterator) traverser.traverse(v2).iterator();

		boolean traverseV1Ancestors = true;
		boolean traverseV2Ancestors = false;

		int currentDepthOfV1Traversal = 0;
		int currentDepthOfV2Traversal = 0;

		Set<MySqlVertex> v1Ancestors = new HashSet<MySqlVertex>();
		Set<MySqlVertex> v2Ancestors = new HashSet<MySqlVertex>();
		Set<MySqlVertex> lowestCommonAncestors = new HashSet<MySqlVertex>();

		// TODO : refactor - repeated code.

		// Traverses ancestors of both v1 and v2, in turns. 
		while ((traverseV1Ancestors && v1Traverser.hasNext()) || (traverseV2Ancestors && v2Traverser.hasNext())) {

			if (traverseV1Ancestors) {

				// Traverses v1.
				MySqlVertex v1Ancestor = v1Traverser.next();
				currentDepthOfV1Traversal = (int) v1Traverser.getDepth();

				// If not outside depth boundaries,
				if (currentDepthOfV1Traversal <= maxDepth) {
					v1Ancestors.add(v1Ancestor);

					// and if ancestor of v1 is ancestor of v2,
					if (v2Ancestors.contains(v1Ancestor)) {

						// then adds it to the set of lowest common ancestors.
						lowestCommonAncestors.add(v1Ancestor);

						// Updates maximum depth.
						if (currentDepthOfV1Traversal <= maxDepth) {
							maxDepth = currentDepthOfV1Traversal;
						}
					}

					// and flips which vertex is traversed, if can traverse the other.
					if (currentDepthOfV2Traversal <= currentDepthOfV1Traversal) {
						traverseV1Ancestors = false;
						traverseV2Ancestors = true;
					}
				}

				// Otherwise attempts to flip which vertex is traversed.
				else {
					traverseV1Ancestors = false;
					if (currentDepthOfV2Traversal <= maxDepth) {
						traverseV2Ancestors = true;
					}
				}
			}
			else if (traverseV2Ancestors) {

				// Traverses v2.
				MySqlVertex v2Ancestor = v2Traverser.next();
				currentDepthOfV2Traversal = (int) v2Traverser.getDepth();

				// If not outside depth boundaries,
				if (currentDepthOfV1Traversal <= maxDepth) {
					v2Ancestors.add(v2Ancestor);

					// and if ancestor of v2 is ancestor of v1,
					if (v1Ancestors.contains(v2Ancestor)) {

						// then adds it to the set of lowest common ancestors.
						lowestCommonAncestors.add(v2Ancestor);

						// Updates maximum depth.
						if (currentDepthOfV2Traversal <= maxDepth) {
							maxDepth = currentDepthOfV2Traversal;
						}
					}

					// and flips which vertex is traversed, if can traverse the other.
					if (currentDepthOfV1Traversal <= currentDepthOfV2Traversal) {
						traverseV2Ancestors = false;
						traverseV1Ancestors = true;
					}
				}

				// Otherwise attempts to flip which vertex is traversed.
				else {
					traverseV2Ancestors = false;
					if (currentDepthOfV1Traversal <= maxDepth) {
						traverseV1Ancestors = true;
					}
				}

			}
		}
		
		return lowestCommonAncestors;
	}

	@Override
	public Iterable<MySqlVertex> findNeighbours(MySqlVertex start) {
		ArrayList<String> empty = new ArrayList<String>();
		return createTraverser(1, 1, empty, Direction.BOTH).traverse(start);
	}

	@Override
	public Iterable<MySqlVertex> findNeighbours(MySqlVertex start, int atDepth) {
		ArrayList<String> empty = new ArrayList<String>();
		return createTraverser(atDepth, atDepth, empty, Direction.BOTH).traverse(start);
	}

	private void createMarkDepthColumn() throws SQLException {
		Statement statement = graph.getMySqlConnection().createStatement();
		try {

			// Drops markDepthColumn if it already existed.
			try {
				statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " DROP " + MARK_DEPTH_COLUMN);

				// Dropped it.
				System.err.println("Dropped existing column " + MARK_DEPTH_COLUMN + " in findNeighbours ");
			}
			catch (SQLException e) {

				// Ignores, as the column shouldn't exist.
			} finally {

				// Adds the column.
				statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " ADD " + MARK_DEPTH_COLUMN + " BIGINT ;");	
			}


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

			// Gets the child id and its depth.
			long child = resultSet.getLong(1);
			long childDepth = getDepth(child);

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
