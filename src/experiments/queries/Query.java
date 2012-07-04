package experiments.queries;

import java.util.ArrayList;

import util.Printer;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public abstract class Query {
	
	/**
	 * 
	 * @param graph
	 * 
	 * @return time in ms.
	 * 
	 */
	public abstract <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph);
	
	public abstract ArrayList<Long> getQueryVertices();
	
	public <V extends IVertex, E extends IEdge> V getVertex(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("numbers").getFirst("number", vertexNumber + "");
	}
	
	public abstract String getPrintDetials();

}
