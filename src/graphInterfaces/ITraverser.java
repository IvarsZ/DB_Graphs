package graphInterfaces;


public interface ITraverser<V extends IVertex> {
	

	public abstract Iterable<V> traverse(final V vertex);
}
