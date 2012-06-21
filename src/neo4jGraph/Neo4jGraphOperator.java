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
	public Neo4jVertex findAncestor(Neo4jVertex v1, Neo4jVertex v2, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) {

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
		Set<Node> v2Ancestors = new HashSet<Node>();
		
		// Traverses both nodes,
		Iterator<Node> v1Traverser = td.traverse(v1.getNode()).nodes().iterator();
		Iterator<Node> v2Traverser = td.traverse(v2.getNode()).nodes().iterator();
		
		// while there are nodes left,
		while (v1Traverser.hasNext() && v2Traverser.hasNext()) {
			
			// Gets the next ancestor of v1.
			Node v1Ancestor = v1Traverser.next();
			
			// If it is ancestor of v2, 
			if (v2Ancestors.contains(v1Ancestor)) {
				
				// then it is lowest common ancestor. 
				return new Neo4jVertex(v1Ancestor, graph);
			}
			else {
				
				// otherwise adds it to v1 ancestors.
				v1Ancestors.add(v1Ancestor);
			}
			
			// Gets the next ancestor of v2.
			Node v2Ancestor = v2Traverser.next();
			
			// If it as ancestor of v1,
			if (v1Ancestors.contains(v2Ancestor)) {
				
				// then it is lowest common ancestor.
				return new Neo4jVertex(v2Ancestor, graph);
			}
			else {
				
				// otherwise adds it to the v2 ancestors.
				v2Ancestors.add(v2Ancestor);
			}
		}
		
		// No traversed ancestors were common between v1 and v2, so it has none.
		return null;
	}
}
