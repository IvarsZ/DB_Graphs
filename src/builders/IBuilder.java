package builders;

import java.util.ArrayList;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

// TODO : rename
public interface IBuilder {
	
	// TODO : where to capture parameters.
	// TODO : indexing, accessing later?
	
	public <V extends IVertex, E extends IEdge> void  build(long seed, IPersistentGraph<V, E> graph);
	
	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(IPersistentGraph<V, E> graph);
	
	public <V extends IVertex> void addVerticesToQuery(ArrayList<V> queryVertices);

}
