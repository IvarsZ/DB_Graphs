package graphInterfaces;

public interface IIndexManager<V extends IVertex, E extends IEdge> {
	
	/**
	 * 
	 * Returns an index for vertices with the specified name.
	 * If such index doesn't exist it is created.
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
	 * @param indexName
	 * 
	 * @return
	 * 
	 */
	IIndex<E> forEdges(String indexName);

}