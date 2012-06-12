package graphsInterfaces;

import java.util.Map.Entry;
import java.util.Set;


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
	 * Adds a new vertex to the graph with the specified id,
	 * if the id is already used by the graph, doesn't add a new vertex,
	 * just returns the vertex specified by the id.
	 * 
	 * @param id - integer identifier of the vertex to add.
	 * 
	 * @return the newly created vertex or the vertex specified by the id.
	 * 
	 */
	public IVertex addVertex(int id);
	

	/**
	 * Creates and edge between the start and end vertex, only if both belong to the graph and
	 * if the specified id of the edge isn't already used by another edge.
	 * 
	 * @param id - integer identifier of the edge to create.
	 * @param start - the start vertex of the edge.
	 * @param end - the end vertex of the edge.
	 * 
	 * @return the created Edge, or the edge specified by the id.
	 * 
	 * @throws IllegalArgumentException
	 * 
	 */
	public IEdge createEdge(int id, IVertex start, IVertex end) throws IllegalArgumentException;
	

	/**
	 * 
	 * Gets a vertex that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public IVertex getVertex(int id);

	/**
	 * 
	 * Gets an edge that has the specified id in the graph.
	 * 
	 * @param id of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 * 
	 */
	public IEdge getEdge(int id);
	

	/**
	 * 
	 * Gets all vertices in the graph.
	 * 
	 * @return set of integer id and vertex pairs.
	 * 
	 */
	public Set<Entry<Integer, IVertex>> getVertices();
	
	/**
	 *
	 * Gets all edges in the graph.
	 * 
	 * @return set of integer id and edge pairs.
	 *
	 */
	public Set<Entry<Integer, IEdge>> getEdges();
	
	/**
	 * 
	 * Deletes all nodes and edges.
	 * 
	 */
	public void clear();
}