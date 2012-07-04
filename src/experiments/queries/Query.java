package experiments.queries;

import java.util.ArrayList;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public abstract class Query {
	
	public abstract <V extends IVertex, E extends IEdge> void execute(IPersistentGraph<V, E> graph);
	
	public abstract ArrayList<Long> getQueryVertices();
	
	public <V extends IVertex, E extends IEdge> V getVertex(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("numbers").getFirst("number", vertexNumber + "");
	}
	
	protected <V extends IVertex, E extends IEdge> void print(long time, IPersistentGraph<V, E> graph) {
		System.out.println("For graph " + graph.getName() + " LCA time is " + time);
	}
}
