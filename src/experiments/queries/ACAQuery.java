package experiments.queries;

import experiments.builders.Indexer;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;
import graphInterfaces.IPersistentGraph.Direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * All Common ancestors query.
 * 
 * @author iz2
 *
 */
public class ACAQuery implements IQueryTemplate {
	
	private long v1Number;
	private long v2Number;
	int maxDepth;
	private ArrayList<String> allowedEdgeTypes;
	private Direction allowedDirection;

	/**
	 * Creates a common ancestors query with the given arguments.
	 */
	public ACAQuery(long v1Number, long v2Number, int maxDepth, ArrayList<String> allowedEdgeTypes, Direction allowedDirection) {
		
		this.v1Number = v1Number;
		this.v2Number = v2Number;
		this.maxDepth = maxDepth;
		this.allowedEdgeTypes = allowedEdgeTypes;
		this.allowedDirection = allowedDirection;
	}
	
	public <V extends IVertex, E extends IEdge> IPreparedQuery prepare(final IPersistentGraph<V, E> graph) {
		
		return new IPreparedQuery() {
			
			V v1 = Indexer.getVertex(v1Number, graph);
			V v2 = Indexer.getVertex(v2Number, graph);

			@Override
			public void execute() {
				graph.getOperator().findCommonAncestors(v1, v2, maxDepth, allowedEdgeTypes, allowedDirection);
			}
			
		};
	}

	@Override
	public Set<Long> getQueryVertices() {
		
		Set<Long> queryVertices = new HashSet<Long>();
		
		queryVertices.add(v1Number);
		queryVertices.add(v2Number);
		
		return queryVertices;
	}

	@Override
	public String getPrintDetials() {
		
		return "CA (v1 " + v1Number + ", v2 " + v2Number + ", max depth " + maxDepth + ")";
	}

}
