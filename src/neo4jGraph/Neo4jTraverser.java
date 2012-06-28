package neo4jGraph;

import graphInterfaces.ITraverser;

import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class Neo4jTraverser implements ITraverser<Neo4jVertex> {

	TraversalDescription td = Traversal.description();
	Neo4jGraph graph;

	protected Neo4jTraverser (int minDepth, int maxDepth, List<String> allowedEdgeTypes, graphInterfaces.IPersistentGraph.Direction allowedDirection) {
		td = Traversal.description();
		td = td.breadthFirst();
		td = td.evaluator(Evaluators.fromDepth(minDepth));
		td = td.evaluator(Evaluators.toDepth(maxDepth));
		
		Direction direction = Neo4jGraph.convertDirection(allowedDirection);
		for (String type : allowedEdgeTypes) {
			td = td.relationships(DynamicRelationshipType.withName(type), direction);
		}
	}
	
	@Override
	public Iterable<Neo4jVertex> traverse(final Neo4jVertex vertex) {


		return new Iterable<Neo4jVertex>() {

			@Override
			public Iterator<Neo4jVertex> iterator() {
				return new Neo4jVertexIterator(td.traverse(vertex.getNode()).nodes().iterator(), graph);
			}
		};
	}
}
