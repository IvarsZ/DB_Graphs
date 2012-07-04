package experiments.queries;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

public abstract class Query {
	
	// TODO : move?
	protected static <V extends IVertex, E extends IEdge> V getVertex(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("numbers").getFirst("number", vertexNumber + "");
	}
	
	/**
	 * 
	 * @param graph
	 * 
	 * @return time in ms.
	 * 
	 */
	public abstract <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph);
	
	public abstract ArrayList<Long> getQueryVertices();
	
	public abstract String getPrintDetials();

}
