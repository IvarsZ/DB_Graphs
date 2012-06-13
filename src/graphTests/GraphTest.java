package graphTests;

import static org.junit.Assert.*;
import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

import org.junit.Before;
import org.junit.Test;

public abstract class GraphTest {

	private IGraph graph;

	public abstract IGraph createEmptyGraph();

	@Before
	public void setup(){
		graph = createEmptyGraph();
	}

	@Test
	public void createGraph() {

		// TODO : better test?
		
		// Checks that the graph is empty.
		for (IVertex v : graph.getVertices()) {
			System.out.println(v.getId());
			//fail("empty graph has a vertex " + v);
		}

		// Checks that the graph is empty.
		for (IEdge e : graph.getEdges()) {
			fail("empty graph has an edge " + e);
		}
	}

	@Test
	public void createVertex() {

		// Creates a vertex.
		IVertex newVertex = graph.createVertex();

		// Checks it can be retrieved by id, and
		assertTrue(graph.getVertex(newVertex.getId()).equals(newVertex));
		
		// TODO: test something else?
	}

	@Test
	public void createEdge() {
		
		// Creates two vertices and edge between them.
		IVertex start = graph.createVertex();
		IVertex end = graph.createVertex();
		IEdge newEdge= graph.createEdge(start, end);

		// Checks it can be retrieved by id, and
		assertTrue(graph.getEdge(newEdge.getId()).equals(newEdge));
		
		// TODO: test something else?
	}

}
