package neighbourFinders;

import graphs.PropertyGraph;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class Neo4jNeighbourFinder {

	/**
	 * Finds all neighbours of a given node. The neighbours are all nodes that have
	 * either ingoing or outgoing relationship with the node.
	 * 
	 * @param node - node whose neighbours are found. It 
	 */
	public Set<Node> findNeighbours(Node node) {

		Set<Node> neighbours = new HashSet<Node>();

		// Iterates over all relationships of this node,
		for (Relationship r : node.getRelationships()) {

			// adds the other node of each relationship to the set of neighbours, except references.
			if (isReference(r) == false) {
				neighbours.add(r.getOtherNode(node));
			}
		}

		return neighbours;
	}
	
	private boolean isReference(Relationship relationship) {
		return relationship.isType(PropertyGraph.GraphRelationshipTypes.REFERENCE_TO_NODE);
	}

	private class PruneNodeReferences implements Evaluator {

		private PruneNodeReferences() {
		}

		@Override
		public Evaluation evaluate(Path path) {

			Relationship last = path.lastRelationship();
			if (last != null && isReference(last)) {
				return Evaluation.EXCLUDE_AND_PRUNE;
			}

			return Evaluation.INCLUDE_AND_CONTINUE;
		}
	}

	/**
	 * 
	 * Ignores directions, too.
	 * 
	 * @param start
	 * @param atDepth
	 * 
	 * @return
	 */
	public Set<Node> findNeighbours(Node start, int atDepth) {

		Set<Node> neighbours = new HashSet<Node>();

		// Traverses nodes that are atDepth from the start by using BFS and pruning all REFERENCE_TO_NODE relationships.
		TraversalDescription td = Traversal.description();
		td = td.breadthFirst();
		td = td.evaluator(Evaluators.atDepth(atDepth));
		td = td.evaluator(new PruneNodeReferences());

		// Adds all traversed nodes to the neighbours set.
		for (Path traversePath : td.traverse(start)) {
			neighbours.add(traversePath.endNode());
		}

		return neighbours;
	}
}
