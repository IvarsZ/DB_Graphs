package graphs;

import graphsInterfaces.IEdge;
import graphsInterfaces.IGraph;
import graphsInterfaces.IVertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public IVertex addVertex(int id) {

		// Checks if the id is already used.
		IVertex vertex = vertices.get(id);
		if (vertex != null) {
			
			// Returns the vertex specified by the id.
			return vertex;
		}
		
		// Creates an new vertex and adds to vertices with the id.
		vertex = new Vertex(id);
		vertices.put(id, vertex);
		return vertex;
	}

	@Override
	public IEdge createEdge(int id, IVertex start, IVertex end) {

		// Checks if both vertices are in the graph.
		if (vertices.containsValue(start) && vertices.containsValue(end)) {

			// Checks if the id is already used.
			IEdge edge = edges.get(id);
			if (edge != null) {
				
				// Returns the edge specified by the id.
				return edge;
			}
			
			// Creates an new edge and adds to vertices with the id.
			edge = new Edge(start, end);
			edges.put(id, edge);
			return edge;
		}
		else {
			throw new IllegalArgumentException("Attempting to create an edge between two vertices of which at least one doesn't belong to the graph");
		}
	}
	
	@Override
	public IVertex getVertex(int index) {
		return vertices.get(index);
	}
	
	@Override
	public IEdge getEdge(int i) {
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
	public Set<Entry<Integer, IVertex>> getVertices() {
		return vertices.entrySet();
	}

	@Override
	public Set<Entry<Integer, IEdge>> getEdges() {
		return edges.entrySet();
	}
}
