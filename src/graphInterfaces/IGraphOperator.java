package graphInterfaces;

import graphInterfaces.IPersistentGraph.Direction;

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
	
	public ITraversalDescription<V> createTraverser(int minDepth, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection);

	/**
	 * 
	 * Finds all neighbours of a given vertex. The neighbours are all vertices that have
	 * either incomming or outgoing relationship with the vertex.
	 * 
	 * @param start - vertex whose neighbours are found.
	 * 
	 */
	public Iterable<V> findNeighbours(V start);
	
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
	public Iterable<V> findNeighbours(V start, int atDepth);
	
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
	public Set<V> findCommonAncestors(V v1, V v2, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection);

	/**
	 * 
	 * Finds the lowest common ancestor(s) of two vertices up to certain depth.
	 * 
	 * A common ancestor is a vertex which has a path to the both vertices. A vertex is lowest
	 * common ancestor if for no other common ancestor both paths to the two vertices are shorter.
	 * 
	 * @param v1
	 * @param v2
	 * @param maxDepth
	 * @param allowedEdgeTypes
	 * @param allowedDirection
	 * @return
	 */
	public Set<V> findLowestCommonAncestors(V v1, V v2, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection);
}
