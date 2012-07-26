package experiments.queries;

import java.util.Set;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;


/**
 * 
 * interface for queries used in experiments.
 * 
 * @author iz2
 *
 */
public interface IQueryTemplate {
	

	public abstract <V extends IVertex, E extends IEdge> IPreparedQuery prepare(final IPersistentGraph<V, E> graph);
	
	/**
	 * 
	 * Gets the vertices used in the query.
	 * 
	 * @return arraylist containing all vertices used in the query.
	 */
	public abstract Set<Long> getQueryVertices();
	
	public abstract String getPrintDetials();

}
