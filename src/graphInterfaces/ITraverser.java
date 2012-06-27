package graphInterfaces;

import graphInterfaces.IPersistentGraph.Direction;

import java.util.List;

public interface ITraverser<V extends IVertex> {
	

	public abstract Iterable<V> traverse(final V vertex);

	public abstract void setMaxDepth(int maxDepth);

	public abstract void setAllowedEdgeTypes(List<String> allowedEdgeTypes);

	public abstract void setAllowedDirection(Direction allowedDirection);
}
