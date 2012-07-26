package experiments.builders;

import java.util.Set;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;


/**
 * 
 * Builder for populating a graph with vertices and edges.
 * The graph is generated pseudo-randomly using a seed.
 * 
 * If all parameters are the same (except the seed), then
 * the graphs should have a similar structure.
 * 
 * @author iz2
 *
 */
public interface IBuilder {

	/**
	 * 
	 * Builds a graph by adding edges and vertices to it.
	 * 
	 * @param seed - seed to use when generating vertices and edges.
	 * @param graph - graph to which edges and vertices are added.
	 * 
	 */
	public <V extends IVertex, E extends IEdge> void build(long seed,
			IPersistentGraph<V, E> graph);

	/**
	 * 
	 * Checks if a graph has already been built.
	 * 
	 * @param graph - the graph to check.
	 * @param seed - the seed of the build to check.
	 * 
	 * @return whether the graph has already been built.
	 */
	public <V extends IVertex, E extends IEdge> boolean isWrittenTo(
			IPersistentGraph<V, E> graph, long seed);

	/**
	 * 
	 * Adds a vertex for indexing when a graph is built.
	 * 
	 * @param verticesToIndex - number of the vertex to index.
	 * 
	 * @throws IllegalArgumentException if the number of the vertex is larger than the number of vertices.
	 */
	public void addVerticesToIndex(Set<Long> verticesToIndex)
			throws IllegalArgumentException;
	
	/**
	 * @return string with builder's details.
	 */
	public String getPrintDetails();

}