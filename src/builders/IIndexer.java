package builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public interface IIndexer {
	
	public <V extends IVertex, E extends IEdge> void index(V vertex, IPersistentGraph<V, E> graph); 

}
