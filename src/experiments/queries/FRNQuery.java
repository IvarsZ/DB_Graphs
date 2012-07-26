package experiments.queries;

import experiments.builders.Indexer;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * Find real neighbours query (at depth 1).
 * 
 * @author iz2
 *
 */
public class FRNQuery implements IQueryTemplate {

	private long vNumber;

	/**
	 * Creates a common ancestors query with the given arguments.
	 */
	public FRNQuery(long vNumber) {

		this.vNumber = vNumber;
	}

	@Override
	public String getPrintDetials() {

		return "FRN (v " + vNumber + ")";
	}

	@Override
	public Set<Long> getQueryVertices() {
		
		Set<Long> queryVertices = new HashSet<Long>();
		
		queryVertices.add(vNumber);
		
		return queryVertices;
	}

	@Override
	public <V extends IVertex, E extends IEdge> IPreparedQuery prepare(final IPersistentGraph<V, E> graph) {

		return new IPreparedQuery() {

			private V v = Indexer.getVertex(vNumber, graph);

			@Override
			public void execute() {
				graph.getOperator().findNeighbours(v);
			}
		};
	}

}
