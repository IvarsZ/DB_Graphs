package experiments.queries;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

/**
 * 
 * interface for queries used in experiments.
 * 
 * @author iz2
 *
 */
public interface IQuery {
	
	
	/**
	 * 
	 * Executes the query on a give graph.
	 * 
	 * @param graph - the given graph.
	 * 
	 * @return execution time in ms.
	 * 
	 */
	public abstract <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph);
	
	/**
	 * 
	 * Gets the vertices used in the query.
	 * 
	 * @return arraylist containing all vertices used in the query.
	 */
	public abstract ArrayList<Long> getQueryVertices();
	
	public abstract String getPrintDetials();

}
