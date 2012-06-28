package graphInterfaces;


public interface ITraverser<V extends IVertex> {
	
	// TODO : capture traversal properties here?
	
	public abstract Iterable<V> traverse(final V vertex);
}
