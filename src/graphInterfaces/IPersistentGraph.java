package graphInterfaces;

import exceptions.DataAccessException;

/**
 * 
 * An interface for a persistent property graph that can be converted to neo4j and MySql.
 * 
 * A graph is set of objects called vertices, where pairs of vertices can be connected.
 * Such connection is called edge. An edge has a direction, so one vertex is the start and one is the 
 * end of the edge.
 * 
 * In a property graph vertices and edges can have properties. A property is key-value pair.
 * 
 * @author iz2
 *
 */
public interface IPersistentGraph<V extends IVertex, E extends IEdge> {
	
	public enum Direction {
		INCOMING,
		OUTGOING,
		BOTH
	}
	
	/**
	 * 
	 * Creates a new vertex with no properties in the graph. 
	 * 
	 * @return - the newly created vertex.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public V createVertex() throws DataAccessException;

	/**
	 * 
	 * Creates and edge between the start and end vertex, only if both belong to the graph.
	 * 
	 * @param start - the start vertex of the edge.
	 * @param end - the end vertex of the edge.
	 * @param type - the tpye of the edge.
	 * 
	 * @return the created Edge, or null if an edge wasn't created.
	 * 
	 * @throws IllegalArgumentException - if at least one of the vertexes doesn't belong to the graph.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public E createEdge(V start, V end, String type) throws IllegalArgumentException, DataAccessException;
	

	/**
	 * 
	 * Gets a vertex that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public V getVertex(long id) throws DataAccessException;

	/**
	 * 
	 * Gets an edge that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public E getEdge(long id) throws DataAccessException;

	/**
	 * 
	 * Gets all vertices in the graph.
	 * 
	 * @return a set of vertices.
	 * 
	 */
	public Iterable<V> getVertices();
	
	/**
	 * 
	 * Gets the index manager associated with this graph.
	 * 
	 * @return
	 * 
	 */
	public IIndexManager<V, E> index();
	
	/**
	 *
	 * Gets all edges in the graph.
	 * 
	 * @return a set of edges.
	 *
	 */
	public Iterable<E> getEdges();
	
	/**
	 * 
	 * Creates and returns a new instance of graph operator. TODO : change to one instance.
	 * 
	 * @return
	 */
	public IGraphOperator<V, E> getOperator();
	
	/**
	 * 
	 * Deletes all nodes and edges.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public void clear() throws DataAccessException;
	
	/**
	 * 
	 * Commits the last changes made to the graph.
	 * Makes all changes since the last commit persistent.
	 * Only operation that does not require a commit is clear.
	 * 
	 * Other instances of the same graph, see the changes only
	 * when they are committed.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public void commit() throws DataAccessException;
	
	/**
	 * 
	 * Rolls back the last changes made to the graph.
	 * Cancels all changes done since the last commit.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public void rollback() throws DataAccessException;
	
	/**
	 * Properly closes the graph, releasing its resources.
	 * 
	 * All uncommitted changes are rolled back.
	 * 
	 * @throws DataAccessException 
	 * 
	 */
	public void close() throws DataAccessException;
}
