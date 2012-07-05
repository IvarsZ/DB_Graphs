package experiments.queries;

import experiments.builders.Indexer;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;
import graphInterfaces.IPersistentGraph.Direction;

import java.util.ArrayList;

/**
 * 
 * Common ancestors query.
 * 
 * @author iz2
 *
 */
public class CAQuery implements IQuery {
	
	private long v1Number;
	private long v2Number;
	int maxDepth;
	private ArrayList<String> allowedEdgeTypes;
	private Direction allowedDirection;

	/**
	 * Creates a common ancestors query with the given arguments.
	 */
	public CAQuery(long v1Number, long v2Number, int maxDepth, ArrayList<String> allowedEdgeTypes, Direction allowedDirection) {
		
		this.v1Number = v1Number;
		this.v2Number = v2Number;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;
	}

	@Override
	public <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph) {

		// Gets required vertices.
		V v1 = Indexer.getVertex(v1Number, graph);
		V v2 = Indexer.getVertex(v2Number, graph);

		// Executes the query and captures the time.
		long start, end;
		start = System.currentTimeMillis();
		graph.getOperator().findCommonAncestors(v1, v2, maxDepth, allowedEdgeTypes, allowedDirection);
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

	@Override
	public String getPrintDetials() {
		
		return "CA (v1 " + v1Number + ", v2 " + v2Number + ", max depth " + maxDepth + ")";
	}

}
