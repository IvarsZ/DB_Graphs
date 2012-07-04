package experiments.queries;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

import java.util.ArrayList;

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

	public LCAQuery(long v1Number, long v2Number, int maxDepth, ArrayList<String> allowedEdgeTypes, Direction allowedDirection) {
		
		this.v1Number = v1Number;
		this.v2Number = v2Number;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;
	}

	public <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph) {

		// Gets required vertices.
		V v1 = getVertex(v1Number, graph);
		V v2 = getVertex(v2Number, graph);

		// Executes the query and captures the time.
		long start, end;
		start = System.currentTimeMillis();
		graph.getOperator().findLowestCommonAncestors(v1, v2, maxDepth, allowedEdgeTypes, allowedDirection);
		end = System.currentTimeMillis();

		return end - start;
	}

	@Override
	public ArrayList<Long> getQueryVertices() {
		
		ArrayList<Long> queryVertices = new ArrayList<Long>();
		
		queryVertices.add(v1Number);
		queryVertices.add(v2Number);
		
		return queryVertices;
	}

	public String getPrintDetials() {
		
		return "LCA (v1 " + v1Number + ", v2 " + v2Number + ", max depth " + maxDepth + ")";
	}
}
