package builders;

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
public class RandomTreeBuilder  {

	public static final String NUMBER_KEY = "number";

	private static final String SIZE_KEY = "total vertex count";

	private long size;
	private int spawnCount;
	private ArrayList<Long> verticesToIndex;

	/**
	 * 
	 * Creates a new tree build with specific parameters.
	 * 
	 * @param size - number of vertices in the tree.
	 * @param spawnCount - maximum number of vertices to which a new vertex can be attached to.
	 * 
	 */
	public RandomTreeBuilder(long size, int spawnCount) {
		this.size = size;
		this.spawnCount = spawnCount;
	}

	public <V extends IVertex, E extends IEdge> void build(long seed, IPersistentGraph<V, E> graph) {

		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);

		ArrayList<V> spawnPoints = new ArrayList<V>(spawnCount);

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
			if (spawnPoints.size() >= spawnCount) {

				// removes a random spawn point vertex.
				spawnPoints.remove((int) (randomGenerator.next() % spawnPoints.size()));
			}

			// Ads the new vertex to spawning points.
			spawnPoints.add(vertex);
		}

		// Writes how many vertices were written.
		root.setProperty("total vertex count", size + "");

		graph.commit();
	}

	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(IPersistentGraph<V, E> graph) {

		// Gets the root and checks the size. // TODO : write check other parameters?
		V root = graph.index().forVertices("roots").getFirst("root", "root1");
		if (root != null) {
			return (size + "").equals(root.getProperty(SIZE_KEY));
		}

		return false;
	}

	public void addVerticesToIndex(ArrayList<Long> verticesToIndex) {

		// For every vertex,
		for (Long vertexNumber : verticesToIndex) {

			// if it hasn't been added,
			if (verticesToIndex.contains(vertexNumber) == false) {

				// adds it to the vertices to index.
				verticesToIndex.add(vertexNumber);
			}
		}
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
