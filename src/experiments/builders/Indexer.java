package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public class Indexer {
	
	protected static final String NUMBER_KEY = "number";
	private static final String NUMBER_INDEX = "numbers";
	
	public static <V extends IVertex, E extends IEdge> V getVertex(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices(NUMBER_INDEX).getFirst(NUMBER_KEY, vertexNumber + "");
	}
	
	public static <V extends IVertex, E extends IEdge> void indexVertex(long vertexNumber, V vertex, IPersistentGraph<V, E> graph) {
		graph.index().forVertices(NUMBER_INDEX).add(vertex, NUMBER_KEY, vertexNumber + "");
	}

}
