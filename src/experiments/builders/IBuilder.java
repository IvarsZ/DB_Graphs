package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

public interface IBuilder {

	public <V extends IVertex, E extends IEdge> void build(long seed,
			IPersistentGraph<V, E> graph);

	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(
			IPersistentGraph<V, E> graph, long seed);

	public void addVerticesToIndex(ArrayList<Long> verticesToIndex)
			throws IllegalArgumentException;
	
	public String getPrintDetails();

}