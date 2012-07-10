package experiments.builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

import util.Util;

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
	
	private static final int MAX_SPAWN_POINTS = 1000000;

	private static final String DEPTH_KEY = "depth";

	private int depth;

	/**
	 * 
	 * Creates a new random tree builder with specific parameters.
	 * 
	 * @param size - number of vertices in the tree.
	 * @param depth - depth of the tree.
	 * 
	 */
	public RandomTree(long size, int depth) throws IllegalArgumentException {
		super(size);
		
		if (size < depth) {
			throw new IllegalArgumentException("size " + size + " smaller than depth " + depth);
		}
		
		
		this.depth = depth;
	}

	@Override
	public <V extends IVertex, E extends IEdge> void build(long seed, IPersistentGraph<V, E> graph) {

		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);

		ArrayList<V> spawnPoints = new ArrayList<V>();

		// Adds the root, and marks it depth.
		V root = createRoot(0, graph);
		root.setProperty(DEPTH_KEY, 0 + "");
		spawnPoints.add(root);
		
		// then creates a linked list of length depth + 1 starting at root.
		V parent = root;
		for (long i = 1; i < depth + 1; i++) {
			
			spawnPoints.add(parent);
			
			V vertex = createVertex(i, graph);
			vertex.setProperty(DEPTH_KEY, i + "");
			graph.createEdge(parent, vertex, "parent of");
			parent = vertex;
		}

		// For the rest size - 2 vertices,
		for (long i = depth + 1; i < getSize(); i++) {

			// creates it and connects it to a random parent, and
			V vertex = createVertex(i, graph);
			parent = spawnPoints.get((int) Util.mod(randomGenerator.nextLong(), spawnPoints.size()));
			graph.createEdge(parent, vertex, "parent of");
			
			// marks the depth of the vertex.
			int currentDepth = Integer.parseInt(parent.getProperty(DEPTH_KEY)) + 1;
			vertex.setProperty(DEPTH_KEY, currentDepth + "");
			
			// If the current depth hasn't reached maximum,
			if (currentDepth < depth) {
			
				// then adds this node to spawn points.
				spawnPoints.add(vertex);
			}
			
			// If there's too many spawn points, removes one at random.
			if (spawnPoints.size() >= MAX_SPAWN_POINTS) {
				spawnPoints.remove((int) Util.mod(randomGenerator.nextLong(), spawnPoints.size()));
			}
			
			graph.commit();
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
				   (depth + "").equals(root.getProperty(DEPTH_KEY)) &&
				   (seed + "").equals(root.getProperty(SEED_KEY));
		}

		return false;
	}
	
	public String getPrintDetails() {
		return "RandomTree (size " + getSize() + ", depth " + depth + ")";
	}
	
	private void writeDetails(IVertex root, long seed) {
		
		root.setProperty(TYPE_KEY, "RandomTree");
		root.setProperty(SIZE_KEY, getSize() + "");
		root.setProperty(DEPTH_KEY, depth + "");
		root.setProperty(SEED_KEY, seed + "");
	}
}
