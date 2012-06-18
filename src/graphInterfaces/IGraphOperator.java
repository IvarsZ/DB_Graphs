package graphInterfaces;

import java.util.Set;

public interface IGraphOperator {

	/**
	 * Finds all neighbours of a given vertex. The neighbours are all vertices that have
	 * either ingoing or outgoing relationship with the vertex.
	 * 
	 * @param start - vertex whose neighbours are found.
	 */
	public Set<IVertex> findNeighbours(IVertex start);
	
	/**
	 * 
	 * Finds all neighbours of a given vertex at a certain depth. The neighbours are all vertices
	 * that have a path of the depth from the node, in any direction.
	 * 
	 * @param start
	 * @param atDepth
	 * 
	 * @return
	 */
	public Set<IVertex> findNeighbours(IVertex start, int atDepth);
	
}
