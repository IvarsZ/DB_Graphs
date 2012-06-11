package graphsInterfaces;

import converters.MySqlConvertable;
import converters.Neo4jConvertable;

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
public interface IPropertyGraph extends Neo4jConvertable, MySqlConvertable {

	/**
	 * 
	 * Adds a vertex to the graph. If the specified vertex is already in the graph,
	 * it is not added twice.
	 * 
	 * @param vertex - vertex to be added to the graph.
	 */
	public void addVertex(IPropertyVertex vertex);
	

	/**
	 * 
	 * Creates and edge between the start and end vertex, only if both belong to the graph.
	 * 
	 * @param start - the start vertex of the edge.
	 * @param end - the end vertex of the edge.
	 * 
	 * @return the created Edge, or null if an edge wasn't created.
	 */
	public IPropertyEdge createEdge(IPropertyVertex start, IPropertyVertex end);
	

	/**
	 * 
	 * Gets a vertex that has the specified index in the graph.
	 * 
	 * @param index of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 */
	public IPropertyVertex getVertex(int index);

	/**
	 * 
	 * Gets an edge that has the specified index in the graph.
	 * 
	 * @param index of the vertex.
	 * 
	 * @return the vertex with the index, or null if there is no vertex with the index.
	 */
	public IPropertyEdge getEdge(int i);
}