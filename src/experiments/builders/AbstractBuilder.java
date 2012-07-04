package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

public abstract class AbstractBuilder implements IBuilder {
	
	protected static final String SIZE_KEY = "size";
	protected static final String TYPE_KEY = "type";
	protected static final String SEED_KEY = "seed";
	
	private static final String NUMBER_KEY = "number";
	
	private long size;
	
	private ArrayList<Long> verticesToIndex;
	
	protected AbstractBuilder(long size) {
		
		this.size = size;
		
		verticesToIndex = new ArrayList<Long>();
	}

	@Override
	public void addVerticesToIndex(ArrayList<Long> indexesOfVertices) throws IllegalArgumentException {

		// For every vertex,
		for (Long vertexNumber : indexesOfVertices) {
			
			// checks that there are enough vertices for this to be indexed,
			if (vertexNumber >= size) {
				
				// TODO : better message.
				throw new IllegalArgumentException("too high vertex index " + vertexNumber + " for size " + size);
			}

			// and if it hasn't been added,
			if (verticesToIndex.contains(vertexNumber) == false) {

				// adds it to the vertices to index.
				verticesToIndex.add(vertexNumber);
				
			}
		}
	}
	
	protected <V extends IVertex, E extends IEdge> V createVertex(long i, IPersistentGraph<V, E> graph) {

		// Creates a new vertex, and sets its index (number).
		V vertex = graph.createVertex();
		vertex.setProperty(NUMBER_KEY, i + "");

		// If the vertex is used in queries,
		if (verticesToIndex.contains(i)) {

			// Indexes it
			graph.index().forVertices("numbers").add(vertex, "number", i + "");
		}

		return vertex;
	}
	
	protected <V extends IVertex, E extends IEdge> V createRoot(long i, IPersistentGraph<V, E> graph) {
		
		// Creates the root and separately indexes it.
		V root = createVertex(i, graph);
		graph.index().forVertices("roots").add(root, "root", "root" + i);
		
		return root;
	}
	
	protected <V extends IVertex, E extends IEdge> V getRoot(long i, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("roots").getFirst("root", "root" + i);
	}
	
	protected long getSize() {
		return size;
	}

}
