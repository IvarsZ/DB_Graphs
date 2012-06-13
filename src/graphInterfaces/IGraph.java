package graphInterfaces;




/**
 * 
 * An interface for a property graph that can be converted to neo4j and MySql.
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
public interface IGraph {

	/**
	 * 
	 * Creates a new vertex with no properties in the graph. 
	 * 
	 * @return - the newly created vertex.
	 * 
	 */
	public IVertex createVertex();
	

	/**
	 * 
	 * Creates and edge between the start and end vertex, only if both belong to the graph.
	 * 
	 * @param start - the start vertex of the edge.
	 * @param end - the end vertex of the edge.
	 * 
	 * @return the created Edge, or null if an edge wasn't created.
	 * 
	 * @throws IllegalArgumentException - if at least one of the vertexes doesn't belong to the graph.
	 * 
	 */
	public IEdge createEdge(IVertex start, IVertex end) throws IllegalArgumentException;
	

	/**
	 * 
	 * Gets a vertex that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public IVertex getVertex(long id);

	/**
	 * 
	 * Gets an edge that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public IEdge getEdge(long id);
	
	

	/**
	 * 
	 * Gets all vertices in the graph.
	 * 
	 * @return a set of vertices.
	 * 
	 */
	public Iterable<IVertex> getVertices();
	
	/**
	 *
	 * Gets all edges in the graph.
	 * 
	 * @return a set of edges.
	 *
	 */
	public Iterable<IEdge> getEdges();
	
	/**
	 * 
	 * Deletes all nodes and edges.
	 * 
	 */
	public void clear();
}