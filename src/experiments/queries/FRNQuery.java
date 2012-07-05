package experiments.queries;

import experiments.builders.Indexer;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

/**
 * 
 * Find real neighbours query (at depth 1).
 * 
 * @author iz2
 *
 */
public class FRNQuery implements IQuery {

	private long vNumber;

	/**
	 * Creates a common ancestors query with the given arguments.
	 */
	public FRNQuery(long vNumber) {
		
		this.vNumber = vNumber;
	}

	@Override
	public <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph) {

		// Gets required vertices.
		V v = Indexer.getVertex(vNumber, graph);

		// Executes the query and captures the time.
		long start, end;
		start = System.currentTimeMillis();
		graph.getOperator().findNeighbours(v);
		end = System.currentTimeMillis();

		return end - start;
	}

	@Override
	public ArrayList<Long> getQueryVertices() {
		
		ArrayList<Long> queryVertices = new ArrayList<Long>();
		
		queryVertices.add(vNumber);
		
		return queryVertices;
	}

	@Override
	public String getPrintDetials() {
		
		return "FRN (v " + vNumber + ")";
	}

}
