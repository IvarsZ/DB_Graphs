package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

/**
 * 
 * Creates a new pseudo-random tree of certain size (appears to be random but depends on the seed).
 * The tree is actually a heap and the vertices are indexed with numbers in order
 * of their addition to the graph.
 * 
 * @author iz2
 *
 */
public class RandomTreeBuilder implements IBuilder  {

	public static final String NUMBER_KEY = "number";

	private static final String TYPE_KEY = "type";
	private static final String SIZE_KEY = "size";
	private static final String WIDTH_KEY = "width";
	private static final String SEED_KEY = "seed";

	private long size;
	private int width;
	private ArrayList<Long> verticesToIndex;

	/**
	 * 
	 * Creates a new tree build with specific parameters.
	 * 
	 * @param size - number of vertices in the tree.
	 * @param maxWidth - maximum width (vertices on one level) of the tree.
	 * 
	 */
	public RandomTreeBuilder(long size, int maxWidth) {
		this.size = size;
		this.width = maxWidth;
		
		verticesToIndex = new ArrayList<Long>();
	}

	@Override
	public <V extends IVertex, E extends IEdge> void build(long seed, IPersistentGraph<V, E> graph) {

		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);

		ArrayList<V> spawnPoints = new ArrayList<V>(width);

		// Adds the root.
		V root = createVertex(0, graph);
		spawnPoints.add(root);

		// Separately indexes the root.
		graph.index().forVertices("roots").add(root, "root", "root1");

		// For size - 1 vertices,
		for (long i = 1; i < size; i++) {

			// creates it and connects it to a random parent.
			V vertex = createVertex(i, graph);
			int parent = (int) (randomGenerator.next() % spawnPoints.size());
			graph.createEdge(spawnPoints.get(parent), vertex, "parent of");

			// If too many spawn points,
			if (spawnPoints.size() >= width) {

				// removes a random spawn point vertex.
				spawnPoints.remove((int) (randomGenerator.next() % spawnPoints.size()));
			}

			// Ads the new vertex to spawning points.
			spawnPoints.add(vertex);
		}

		// Writes the details of the builder
		writeDetails(root, seed);

		graph.commit();
	}
	
	private void writeDetails(IVertex root, long seed) {
		
		root.setProperty(TYPE_KEY, "RandomTreeBuilder");
		root.setProperty(SIZE_KEY, size + "");
		root.setProperty(WIDTH_KEY, width + "");
		root.setProperty(SEED_KEY, seed + "");
	}

	@Override
	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(IPersistentGraph<V, E> graph, long seed) {

		// Gets the root and checks the type, size, width and seed.
		V root = graph.index().forVertices("roots").getFirst("root", "root1");
		if (root != null) {
			
			return ("RandomTreeBuilder").equals(root.getProperty(TYPE_KEY)) &&
				   (size + "").equals(root.getProperty(SIZE_KEY)) &&
				   (width + "").equals(root.getProperty(WIDTH_KEY)) &&
				   (seed + "").equals(root.getProperty(SEED_KEY));
		}

		return false;
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
	
	public String getPrintDetails() {
		return "RandomTreeBuilder (size " + size + ", width " + width + ")";
	}
	
	private <V extends IVertex, E extends IEdge> V createVertex(long i, IPersistentGraph<V, E> graph) {

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
}
