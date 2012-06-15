package graph;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import scala.Int;

/**
 * 
 * Implementation of a property graph that can be converted to Neo4j and MySql.
 * 
 * @author iz2
 *
 */
public class Graph implements IGraph {

	private Map<Integer, IVertex> vertices;
	private Map<Integer, IEdge> edges;

	/**
	 * Creates empty graph with no nodes and edges.
	 */
	public Graph() {
		vertices = new HashMap<Integer, IVertex>();
		edges = new HashMap<Integer, IEdge>();
	}
	
	@Override
	public IVertex createVertex() {

		// Finds an unused id for the vertex. TODO : better id handling mechanism.
		int id;
		do {
			id = (int) (Math.random() * Int.MaxValue());
		} while (vertices.containsKey(id));
		
		// Creates a new vertex with the id, adds and returns it.
		IVertex vertex = new Vertex(id);
		vertices.put(id, vertex);
		return vertex;
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end) throws IllegalArgumentException {

		// Checks if both vertices are in the graph.
		if (vertices.containsValue(start) && vertices.containsValue(end)) {
			
			// Finds an unused id for the edge. TODO : better id handling mechanism.
			int id;
			do {
				id = (int) (Math.random() * Int.MaxValue());
			} while (edges.containsKey(id));
			
			// Creates a new edge with the id, adds and returns it.
			IEdge edge = new Edge(id, start, end);
			edges.put(id, edge);
			return edge;
		}
		else {
			throw new IllegalArgumentException("Vertex " + start + " or " + end + " doesn't belong to the graph.");
		}
	}
	
	@Override
	public IVertex getVertex(long index) {
		return vertices.get(index);
	}
	
	@Override
	public IEdge getEdge(long i) {
		return edges.get(i);
	}
	
	@Override
	public boolean equals(Object obj) {

		// TODO: implement real equals.
		return super.equals(obj);
	}

	@Override
	public void clear() {
		vertices = new HashMap<Integer, IVertex>();
		edges = new HashMap<Integer, IEdge>();
	}

	@Override
	public Iterable<IVertex> getVertices() {
		
		Set<IVertex> vertices = new HashSet<IVertex>();
		vertices.addAll(this.vertices.values());
		return vertices;
	}

	@Override
	public Iterable<IEdge> getEdges() {
		Set<IEdge> edges = new HashSet<IEdge>();
		edges.addAll(this.edges.values());
		return edges;
	}
}
