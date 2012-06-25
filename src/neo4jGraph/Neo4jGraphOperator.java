package neo4jGraph;

import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.neo4j.graphdb.Direction;
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
	public Set<Neo4jVertex> findCommonAncestors(Neo4jVertex v1, Neo4jVertex v2, int maxDepth, List<String> allowedEdgeTypes, graphInterfaces.IPersistentGraph.Direction allowedDirection) {

		// Traverses the nodes with dfs up to the max depth.
		TraversalDescription td = Traversal.description();
		td = td.depthFirst();
		td = td.evaluator(Evaluators.toDepth(maxDepth));

		Direction direction = Neo4jGraph.convertDirection(allowedDirection);

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

	@Override
	public Set<Neo4jVertex> findLowestCommonAncestors(Neo4jVertex v1, Neo4jVertex v2, int maxDepth, List<String> allowedEdgeTypes, IPersistentGraph.Direction allowedDirection) {

		// Traverse both with bfs, and keep both on the same depth.
		TraversalDescription td = Traversal.description();
		td = td.breadthFirst();
		td = td.evaluator(Evaluators.toDepth(maxDepth));

		Direction direction = Neo4jGraph.convertDirection(allowedDirection);

		// Adds the allowed relationship types to the traverser with the direction.
		for (String allowedType : allowedEdgeTypes) {
			td = td.relationships(DynamicRelationshipType.withName(allowedType), direction);
		}

		// Traverses ancestors of the both nodes
		Iterator<Path> v1Traverser = td.traverse(v1.getNode()).iterator();
		Iterator<Path> v2Traverser = td.traverse(v2.getNode()).iterator();

		boolean traverseV1Ancestors = true;
		boolean traverseV2Ancestors = false;

		int currentDepthOfV1Traversal = 0;
		int currentDepthOfV2Traversal = 0;

		Set<Node> v1Ancestors = new HashSet<Node>();
		Set<Node> v2Ancestors = new HashSet<Node>();
		Set<Neo4jVertex> lowestCommonAncestors = new HashSet<Neo4jVertex>();
		
		// TODO : refactor - repeated code.

		// Traverses ancestors of both v1 and v2, in turns. 
		while ((traverseV1Ancestors && v1Traverser.hasNext()) || (traverseV2Ancestors && v2Traverser.hasNext())) {

			if (traverseV1Ancestors) {

				// Traverses v1.
				Path v1Path = v1Traverser.next();
				currentDepthOfV1Traversal = v1Path.length();

				if (currentDepthOfV1Traversal <= maxDepth) {
					Node v1Ancestor = v1Path.endNode();
					v1Ancestors.add(v1Ancestor);

					// If ancestor of v1 is ancestor of v2,
					if (v2Ancestors.contains(v1Ancestor)) {

						// then adds it to the set of lowest common ancestors.
						lowestCommonAncestors.add(new Neo4jVertex(v1Ancestor, graph));

						// Updates maximum depth.
						if (currentDepthOfV1Traversal <= maxDepth) {
							maxDepth = currentDepthOfV1Traversal;
						}
					}
					
					// flips.
					if (currentDepthOfV2Traversal <= currentDepthOfV1Traversal) {
						traverseV1Ancestors = false;
						traverseV2Ancestors = true;
					}
				}
				else {
					traverseV1Ancestors = false;
					if (currentDepthOfV2Traversal <= maxDepth) {
						traverseV2Ancestors = true;
					}
				}
			}
			else if (traverseV2Ancestors) {

				// Traverses v2.
				Path v2Path = v2Traverser.next();
				currentDepthOfV2Traversal = v2Path.length();

				if (currentDepthOfV1Traversal <= maxDepth) {
					Node v2Ancestor = v2Path.endNode();
					v2Ancestors.add(v2Ancestor);

					// If ancestor of v2 is ancestor of v1,
					if (v1Ancestors.contains(v2Ancestor)) {

						// then adds it to the set of lowest common ancestors.
						lowestCommonAncestors.add(new Neo4jVertex(v2Ancestor, graph));

						// Updates maximum depth.
						if (currentDepthOfV2Traversal <= maxDepth) {
							maxDepth = currentDepthOfV2Traversal;
						}
					}
					
					// flips.
					if (currentDepthOfV1Traversal <= currentDepthOfV2Traversal) {
						traverseV2Ancestors = false;
						traverseV1Ancestors = true;
					}
				}
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
}
