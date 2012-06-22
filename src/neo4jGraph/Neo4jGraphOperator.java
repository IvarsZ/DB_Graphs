package neo4jGraph;

import graphInterfaces.IGraphOperator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.neo4j.graphdb.DynamicRelationshipType;
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

	@Override
	public Set<Neo4jVertex> findAncestors(Neo4jVertex v1, Neo4jVertex v2, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) {

		// Traverses the nodes with bfs up to the max depth.
		TraversalDescription td = Traversal.description();
		td = td.breadthFirst();
		td = td.evaluator(Evaluators.toDepth(maxDepth));

		// Converts the direction to neo4j.
		org.neo4j.graphdb.Direction direction = null;
		switch (allowedDirection) {
		case BOTH :
			direction = org.neo4j.graphdb.Direction.BOTH;
			break;
		case INCOMING :
			direction = org.neo4j.graphdb.Direction.INCOMING;
			break;
		case OUTGOING :
			direction = org.neo4j.graphdb.Direction.OUTGOING;
			break;
		default :
			System.err.println("Invalid direction enum value " + allowedDirection);
			break;
		}
		
		// Adds the allowed relationship types to the traverser with the direction.
		for (String allowedType : allowedEdgeTypes) {
			td = td.relationships(DynamicRelationshipType.withName(allowedType), direction);
		}
		
		// Neo4j nodes work with sets, as they implement hashcode and equals based on id.
		Set<Node> v1Ancestors = new HashSet<Node>();
		
		
		// Traverses all ancestors of the first node,
		Iterator<Node> v1Traverser = td.traverse(v1.getNode()).nodes().iterator();
		while (v1Traverser.hasNext()) {
			
			// and adds them to the v1 ancestors set.
			v1Ancestors.add(v1Traverser.next());
		}
		
		Set<Neo4jVertex> commonAncestors = new HashSet<Neo4jVertex>();
		
		// For each ancestor of the second node,
		Iterator<Node> v2Traverser = td.traverse(v2.getNode()).nodes().iterator();
		while (v2Traverser.hasNext()) {
			
			// adds it to the common ancestors set if is ancestor of v1, too.
			Node v2Ancestor = v2Traverser.next();
			if (v1Ancestors.contains(v2Ancestor)) {
				commonAncestors.add(new Neo4jVertex(v2Ancestor, graph));
			}
		}
		
		// Returns the set of common ancestors.
		return commonAncestors;
	}
}
