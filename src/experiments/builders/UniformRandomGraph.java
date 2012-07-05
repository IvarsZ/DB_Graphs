package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

/**
 * 
 * Builder for creating a pseudo random graph,
 * in which every possible edge occurs independently with some set probability.
 * 
 * The graph can have and edge where start and end vertex is the same.
 * 
 * @author iz2
 *
 */
public class UniformRandomGraph extends AbstractBuilder {

	private static final String EDGE_PROBABILITY = "edgeProbability";

	private double edgeProbability;

	/**
	 * 
	 * Creates a uniform random graph builder with the specified size and
	 * edgeProbability.
	 * 
	 * @param size - number of vertices in the generated graphs.
	 * @param edgeProbability - probability of occurrence of each possible edge.
	 * 
	 */
	public UniformRandomGraph(long size, double edgeProbability) {
		super(size);
		
		// checks that edge probability is in the range.
		if (edgeProbability < 0 || edgeProbability > 1) {
			throw new IllegalArgumentException();
		}

		this.edgeProbability = edgeProbability;
	}


	@Override
	public <V extends IVertex, E extends IEdge> void build(long seed,
			IPersistentGraph<V, E> graph) {

		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);

		// Creates the root (not necessarily connected to all vertices) and indexes it.
		V root = createRoot(0, graph);
		indexVertexTemporary(0, root, graph);

		// Create size - 1 vertices,
		for (long i = 1; i < getSize(); i++) {
			V vertex = createVertex(i, graph);
			
			// and indexes them. TODO : temporary index.
			indexVertexTemporary(i, vertex, graph);
			graph.commit();
		}
		
		// For every edge,
		for (long i = 0; i < getSize(); i++) {
			
			// adds some random edges (possibly none).
			addRandomEdges(i, graph, randomGenerator);
			graph.commit();
		}

		// Writes the details of the builder
		writeDetails(root, seed);
		graph.commit();
	}

	@Override
	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(
			IPersistentGraph<V, E> graph, long seed) {

		// Gets the root and checks the type, size, width and seed.
		V root = getRoot(0, graph);
		if (root != null) {
			
			return ("UniformRandomGraph").equals(root.getProperty(TYPE_KEY)) &&
					(getSize() + "").equals(root.getProperty(SIZE_KEY)) &&
					(edgeProbability + "").equals(root.getProperty(EDGE_PROBABILITY)) &&
					(seed + "").equals(root.getProperty(SEED_KEY));
		}

		return false;
	}

	private void writeDetails(IVertex root, long seed) {

		root.setProperty(TYPE_KEY, "UniformRandomGraph");
		root.setProperty(SIZE_KEY, getSize() + "");
		root.setProperty(EDGE_PROBABILITY, edgeProbability + "");
		root.setProperty(SEED_KEY, seed + "");
	}

	@Override
	public String getPrintDetails() {

		return "UniformRandomGraph (size " + getSize() + ", edge probability " + edgeProbability + ")";
	}

	/**
	 * 
	 * Adds random outgoing edges to a given vertex.
	 * 
	 * @param vertexNumber - the number of the given vertex.
	 * @param graph - the graph to which the vertex belongs.
	 * @param randomGenerator - generator to use for generating random numbers.
	 * 
	 */
	private <V extends IVertex, E extends IEdge> void addRandomEdges(long vertexNumber, IPersistentGraph<V, E> graph, LinearCongruentialGenerator randomGenerator) {

		// Gets the vertex to which random edges are added.
		V start = getVertexTemporary(vertexNumber, graph);
		
		// For all vertices,
		for (long i = 0; i < getSize(); i++) {
			
			// generates (pseudo) random probability p.
			double p = randomGenerator.nextDouble();
			
			// If random p is smaller than edgeProbability,
			if (p < edgeProbability) {
				
				// then adds the edge between start vertex and this vertex.
				V end = getVertexTemporary(i, graph);
				graph.createEdge(start, end, "connected to");
			}
		}
	}
	
	private <V extends IVertex, E extends IEdge> void indexVertexTemporary(long vertexNumber, V vertex, IPersistentGraph<V, E> graph) {
		graph.index().forVertices("tmpNumbers").add(vertex, "number", vertexNumber + "");
	}
	
	private <V extends IVertex, E extends IEdge> V getVertexTemporary(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("tmpNumbers").getFirst("number", vertexNumber + "");
	}

}
