package experiments.queries;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * Lowest common ancestor.
 * 
 * @author iz2
 *
 */
public class LCAQuery extends Query {

	private long v1Number;
	private long v2Number;
	int maxDepth;
	private ArrayList<String> allowedEdgeTypes;
	private Direction allowedDirection;

	public <V extends IVertex, E extends IEdge> void execute(IPersistentGraph<V, E> graph) {

		// Gets required vertices.
		V v1 = getVertex(v1Number, graph);
		V v2 = getVertex(v2Number, graph);

		// Executes the query and captures the time.
		long start = System.currentTimeMillis();
		graph.getOperator().findLowestCommonAncestors(v1, v2, maxDepth, allowedEdgeTypes, allowedDirection);
		long time = System.currentTimeMillis() - start;

		print(time, graph);
	}

	public void printArguments() {

		System.out.println("v1 " + v1Number + " v2 " + v2Number + " max depth " + maxDepth);

		// TODO : necessary?
		System.out.println("on edges " + Arrays.toString(allowedEdgeTypes.toArray()));
		System.out.println("direction " + allowedDirection);
	}

	@Override
	public ArrayList<Long> getQueryVertices() {
		
		ArrayList<Long> queryVertices = new ArrayList<Long>();
		
		queryVertices.add(v1Number);
		queryVertices.add(v2Number);
		
		return queryVertices;
	}
}
