package neo4jGraph;

import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class Neo4jGraphOperator implements IGraphOperator {

	private Neo4jGraph graph;

	public Neo4jGraphOperator(IPersistentGraph neo4jGraph) {

		if (neo4jGraph instanceof Neo4jGraph) {

			graph = (Neo4jGraph) neo4jGraph;

		}
		else {
			throw new IllegalArgumentException("Graph " + neo4jGraph + " isn't neo4j graph");
		}
	}

	@Override
	public Set<IVertex> findNeighbours(IVertex start) {

		if (start instanceof Neo4jVertex) {

			Node node = ((Neo4jVertex) start).getNode();

			Set<IVertex> neighbours = new HashSet<IVertex>();

			// Iterates over all relationships of this node,
			for (Relationship r : node.getRelationships()) {

				neighbours.add(new Neo4jVertex(r.getOtherNode(node), graph));
			}

			return neighbours;
		}
		else {
			throw new IllegalArgumentException("Vertex " + start + " isn't instance of Neo4jVertex");
		}

	}

	@Override
	public Set<IVertex> findNeighbours(IVertex start, int atDepth) {

		if (start instanceof Neo4jVertex) {

			Set<IVertex> neighbours = new HashSet<IVertex>();

			Node node = ((Neo4jVertex) start).getNode();

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
		else {
			throw new IllegalArgumentException("Vertex " + start + " isn't instance of Neo4jVertex");
		}
	}

}
