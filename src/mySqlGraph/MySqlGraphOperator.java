package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.ITraverser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	private MySqlGraph graph;

	protected MySqlGraphOperator(MySqlGraph mySqlGraph) throws SQLException {

		this.graph = (MySqlGraph) mySqlGraph;
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
}
