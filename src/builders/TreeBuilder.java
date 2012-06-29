package builders;

import java.util.ArrayList;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

/**
 * 
 * Heap.
 * 
 * @author iz2
 *
 */
public class TreeBuilder implements IBuilder  {
	
	private long size;
	private int leafCount;
	private long seed;
	private IIndexer indexer;
	
	/**
	 * 
	 * @param size - number of vertices (nodes).
	 * @param leafCount - TODO : really? no, what it really is?
	 */
	public TreeBuilder(long size, int leafCount, long seed, IIndexer indexer) {
		this.size = size;
		this.leafCount = leafCount;
		this.seed = seed;
		this.indexer = indexer;
	}

	@Override
	public <V extends IVertex, E extends IEdge> void  build(IPersistentGraph<V, E> graph) {
		
		// Uses Linear congruential generator.
		LinearCongruentialGenerator randomGenerator =  new LinearCongruentialGenerator(seed);
		
		ArrayList<V> leafs = new ArrayList<V>(leafCount);
		
		// Adds the root.
		V root = graph.createVertex();
		root.setProperty("number", "0");
		indexer.index(root);
		leafs.add(root);
		
		for (long i = 1; i < size; i++) {
			
			V vertex = graph.createVertex();
			vertex.setProperty("number", i + "");
			indexer.index(vertex, graph);
			
			int parent = (int) (randomGenerator.next() % leafs.size());
			graph.createEdge(leafs.get(parent), vertex, "parent of");
			
			if (leafs.size() >= leafCount) {
				// Removes random node.
				leafs.remove((int) (randomGenerator.next() % leafs.size()));
			}
			
			leafs.add(vertex);
		}
		
		graph.commit();
	}
}
