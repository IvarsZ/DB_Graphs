package graphInterfaces;

/**
 * 
 * Interface for graph traversal.
 * 
 * Each implementation has its own defined properties how to
 * traverse the graph.
 * 
 * @author iz2
 *
 * @param <V> - type of vertices/graph to traverse.
 */
public interface ITraversalDescription<V extends IVertex> {
	
	// TODO : capture traversal properties here?
	
	public Iterable<V> traverse(final V vertex);
}
