package graphInterfaces;

import java.util.List;
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
	
	public enum Direction {
		INCOMING,
		OUTGOING,
		BOTH
	}

	/**
	 * 
	 * Finds all neighbours of a given vertex. The neighbours are all vertices that have
	 * either incomming or outgoing relationship with the vertex.
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
	
	/**
	 * 
	 * Finds all common ancestors of two vertices up to certain depth.
	 * 
	 * @param v1
	 * @param v2
	 * @param maxDepth
	 * @param allowedEdgeTypes
	 * @param allowedDirection
	 * 
	 * @return
	 */
	public Set<V> findAncestors(V v1, V v2, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection);
	
}
