package graphInterfaces;

import java.util.Set;

/**
 * 
 * Contains various query methods that can be performed on a graph.
 * 
 * @author iz2
 *
 * @param <V> - vertices of the graph.
 * @param <E> - edges of the graph.
 */
public interface IGraphOperator<V extends IVertex, E extends IEdge> {

	/**
	 * 
	 * Finds all neighbours of a given vertex. The neighbours are all vertices that have
	 * either ingoing or outgoing relationship with the vertex.
	 * 
	 * @param start - vertex whose neighbours are found.
	 * 
	 */
	public Set<V> findNeighbours(V start);
	
	/**
	 * 
	 * Finds all neighbours of a given vertex at a certain depth. The neighbours are all vertices
	 * that have a path of the depth from the node, in any direction.
	 * 
	 * @param start
	 * @param atDepth
	 * 
	 * @return
	 * 
	 */
	public Set<V> findNeighbours(V start, int atDepth);
	
}
