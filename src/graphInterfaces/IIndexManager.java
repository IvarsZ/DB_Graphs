package graphInterfaces;

/**
 * 
 * Interface for managing indexes of vertices and edges.
 * It allows to get and create if absent indexes for
 * vertices and edges.
 * 
 * The indexes are identified with a name, the name must be unique,
 * but only for vertices and edges separately, there can be two indexes
 * with the same name - one for vertices and one for edges.
 * 
 * @author iz2
 *
 * @param <V> - type of vertex.
 * @param <E> - type of edge.
 */
public interface IIndexManager<V extends IVertex, E extends IEdge> {
	
	/**
	 * 
	 * Returns an index for vertices with the specified name.
	 * If such index doesn't exist it is created.
	 * 
	 * Ignores all spaces for the name.
	 * 
	 * @param indexName
	 * 
	 * @return
	 * 
	 */
	IIndex<V> forVertices(String indexName);
	
	/**
	 * 
	 * Returns an index for edges with the specified name.
	 * If such index doesn't exist it is created.
	 * 
	 * Ignores all paces for the name.
	 * 
	 * @param indexName
	 * 
	 * @return
	 * 
	 */
	IIndex<E> forEdges(String indexName);

}
