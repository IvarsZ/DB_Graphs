package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

/**
 * 
 * Creates a new pseudo-random tree of certain size (appears to be random but depends on the seed).
 * 
 * The vertices are indexed with numbers in order of their addition to the graph, and
 * regarding to indexes the tree is actually forms a heap.
 * 
 * @author iz2
 *
 */
public class RandomTree extends AbstractBuilder  {

	private static final String WIDTH_KEY = "width";

	private int width;

	/**
	 * 
	 * Creates a new random tree builder with specific parameters.
	 * 
	 * @param size - number of vertices in the tree.
	 * @param maxWidth - maximum width (vertices on one level) of the tree.
	 * 
	 */
	public RandomTree(long size, int maxWidth) {
		super(size);
		this.width = maxWidth;
	}

	@Override
	public <V extends IVertex, E extends IEdge> void build(long seed, IPersistentGraph<V, E> graph) {

		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);

		ArrayList<V> spawnPoints = new ArrayList<V>(width);

		// Adds the root.
		V root = createRoot(0, graph);
		spawnPoints.add(root);

		// For size - 1 vertices,
		for (long i = 1; i < getSize(); i++) {

			// creates it and connects it to a random parent.
			V vertex = createVertex(i, graph);
			int parent = (int) (randomGenerator.nextLong() % spawnPoints.size());
			graph.createEdge(spawnPoints.get(parent), vertex, "parent of");

			// If too many spawn points,
			if (spawnPoints.size() >= width) {

				// removes a random spawn point vertex.
				spawnPoints.remove((int) (randomGenerator.nextLong() % spawnPoints.size()));
			}

			// Ads the new vertex to spawning points.
			spawnPoints.add(vertex);
		}

		// Writes the details of the builder
		writeDetails(root, seed);

		graph.commit();
	}
	
	@Override
	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(IPersistentGraph<V, E> graph, long seed) {

		// Gets the root and checks the type, size, width and seed.
		V root = getRoot(0, graph);
		if (root != null) {
			
			return ("RandomTree").equals(root.getProperty(TYPE_KEY)) &&
				   (getSize() + "").equals(root.getProperty(SIZE_KEY)) &&
				   (width + "").equals(root.getProperty(WIDTH_KEY)) &&
				   (seed + "").equals(root.getProperty(SEED_KEY));
		}

		return false;
	}
	
	public String getPrintDetails() {
		return "RandomTree (size " + getSize() + ", width " + width + ")";
	}
	
	private void writeDetails(IVertex root, long seed) {
		
		root.setProperty(TYPE_KEY, "RandomTree");
		root.setProperty(SIZE_KEY, getSize() + "");
		root.setProperty(WIDTH_KEY, width + "");
		root.setProperty(SEED_KEY, seed + "");
	}
}
