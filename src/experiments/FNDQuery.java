package experiments;

import experiments.builders.Indexer;
import experiments.queries.IQuery;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

/**
 * 
 * Find neighbours at certain depth query.
 * 
 * @author iz2
 *
 */
public class FNDQuery implements IQuery {
	
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
	public <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph) {

		// Gets required vertices.
		V v = Indexer.getVertex(vNumber, graph);

		// Executes the query and captures the time.
		long start, end;
		start = System.currentTimeMillis();
		graph.getOperator().findNeighbours(v, atDepth);
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
		
		return "FND (v " + vNumber + ", depth " + atDepth + ")";
	}
}
