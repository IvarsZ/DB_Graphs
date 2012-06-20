package neo4jGraph;

import graphInterfaces.IGraphOperator;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class Neo4jGraphOperator implements IGraphOperator<Neo4jVertex, Neo4jEdge> {

	private Neo4jGraph graph;

	protected Neo4jGraphOperator(Neo4jGraph neo4jGraph) {
		graph = (Neo4jGraph) neo4jGraph;
	}
	
	// TODO : vertices from the same graph.

	@Override
	public Set<Neo4jVertex> findNeighbours(Neo4jVertex start) {

		Node node = start.getNode();

		Set<Neo4jVertex> neighbours = new HashSet<Neo4jVertex>();

		// Iterates over all relationships of this node,
		for (Relationship r : node.getRelationships()) {

			neighbours.add(new Neo4jVertex(r.getOtherNode(node), graph));
		}

		return neighbours;
	}

	@Override
	public Set<Neo4jVertex> findNeighbours(Neo4jVertex start, int atDepth) {

		Set<Neo4jVertex> neighbours = new HashSet<Neo4jVertex>();

		Node node = start.getNode();

		// Traverses nodes that are atDepth from the start by using BFS and pruning all REFERENCE_TO_NODE relationships.
		TraversalDescription td = Traversal.description();
		td = td.breadthFirst();
		td = td.evaluator(Evaluators.atDepth(atDepth));

		// Adds all traversed nodes to the neighbours set.
		for (Path traversePath : td.traverse(node)) {
			neighbours.add(new Neo4jVertex(traversePath.endNode(), graph));
		}

		return neighbours;
	}
}
