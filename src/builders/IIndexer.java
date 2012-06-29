package builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public interface IIndexer {
	
	public void index(IVertex vertex, IPersistentGraph<IVertex, IEdge> graph); 

}
