package graphFactories;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public interface IFactory<V extends IVertex, E extends IEdge> {
	
	public IPersistentGraph<V, E> createGraph(String name);
	
	public String getPrintDetails();

}
