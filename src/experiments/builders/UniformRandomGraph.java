package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

/**
 * 
 * Can have and edge between one vertex.
 * 
 * @author iz2
 *
 */
public class UniformRandomGraph extends AbstractBuilder {

	private static final long LARGE_LONG = 100000000000000L;

	private static final String EDGE_PROBABILITY = "edgeProbability";

	private double edgeProbability;

	public UniformRandomGraph(long size, double edgeProbability) {
		super(size);
		
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
		V root = createVertex(0, graph);
		indexVertex(0, root, graph);

		// Create size - 1 vertices,
		for (long i = 1; i < getSize(); i++) {
			V vertex = createVertex(i, graph);
			
			// and indexes them. TODO : temporary index.
			indexVertex(i, vertex, graph);
		}
		
		// For every edge,
		for (long i = 0; i < getSize(); i++) {
			
			// adds some random edges (possibly none).
			addRandomEdges(i, graph, randomGenerator);
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

	private <V extends IVertex, E extends IEdge> void addRandomEdges(long vertexNumber, IPersistentGraph<V, E> graph, LinearCongruentialGenerator randomGenerator) {

		// Gets the vertex to which random edges are added.
		V start = getVertex(vertexNumber, graph);
		
		// For all vertices,
		for (long i = 0; i < getSize(); i++) {
			
			// generates (pseudo) random probability p.
			double p = (randomGenerator.next() % LARGE_LONG) / (LARGE_LONG * 1.0); // FIXME
			System.out.println(p); // TODO : remove.
			
			// If random p is smaller than edgeProbability,
			if (p < edgeProbability) {
				
				// then adds the edge between start vertex and this vertex.
				V end = getVertex(i, graph);
				graph.createEdge(start, end, "connected to");
			}
		}
	}
	
	private <V extends IVertex, E extends IEdge> void indexVertex(long vertexNumber, V vertex, IPersistentGraph<V, E> graph) {
		graph.index().forVertices("tmpNumbers").add(vertex, "number", vertexNumber + "");
	}
	
	private <V extends IVertex, E extends IEdge> V getVertex(long vertexNumber, IPersistentGraph<V, E> graph) {
		return graph.index().forVertices("tmpNumbers").getFirst("number", vertexNumber + "");
	}

}
