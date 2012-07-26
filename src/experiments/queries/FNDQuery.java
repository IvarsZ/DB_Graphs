package experiments.queries;

import experiments.builders.Indexer;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Find neighbours at certain depth query.
 * 
 * @author iz2
 *
 */
public class FNDQuery implements IQueryTemplate {
	
	private long vNumber;
	private int atDepth;

	/**
	 * Creates a common ancestors query with the given arguments.
	 */
	public FNDQuery(long vNumber, int atDepth) {
		
		this.vNumber = vNumber;
		this.atDepth = atDepth;
	}

	@Override
	public Set<Long> getQueryVertices() {
		
		Set<Long> queryVertices = new HashSet<Long>();
		
		queryVertices.add(vNumber);
		
		return queryVertices;
	}

	@Override
	public String getPrintDetials() {
		
		return "FND (v " + vNumber + ", depth " + atDepth + ")";
	}

	@Override
	public <V extends IVertex, E extends IEdge> IPreparedQuery prepare(final IPersistentGraph<V, E> graph) {
		
		return new IPreparedQuery() {
			
			V v = Indexer.getVertex(vNumber, graph);
			
			@Override
			public void execute() {
				graph.getOperator().findNeighbours(v, atDepth);
			}
		};
	}
}
