package builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public interface IBuilder {
	
	// TODO : where to capture parameters.
	// TODO : indexing, accessing later?
	
	public <V extends IVertex, E extends IEdge> void  build(IPersistentGraph<V, E> graph);

}
