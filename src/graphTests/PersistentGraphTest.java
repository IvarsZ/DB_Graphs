package graphTests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class PersistentGraphTest {

	protected IPersistentGraph graph;

	/**
	 * 
	 * Might just reopen, not guaranteed to be empty.
	 * 
	 * @return
	 */
	public abstract IPersistentGraph createGraph();

	@Before
	public void setup(){
		graph = createGraph();
		graph.clear();
	}

	@After
	public void cleanup() {
		graph.clear();
		graph.close();
	}

	@Test
	public void createEmptyGraphTest() {

		// Checks that the graph is empty.
		for (IVertex v : graph.getVertices()) {
			System.out.println(v.getId());
			fail("empty graph has a vertex " + v + " " + v.getId());
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

		// Checks it can be retrieved by id.
		assertTrue(graph.getVertex(newVertex.getId()).equals(newVertex));
	}

	@Test
	public void createPersistentVertex() {

		// Creates a vertex, and closes the graph.
		IVertex newVertex = graph.createVertex();
		graph.commit();
		graph.close();

		// Reopens the graph, checks that the vertex is there.
		graph = createGraph();
		assertTrue(graph.getVertex(newVertex.getId()).equals(newVertex));
	}

	@Test
	public void createEdge() {

		// Creates two vertices and edge between them.
		IVertex start = graph.createVertex();
		IVertex end = graph.createVertex();
		IEdge newEdge = graph.createEdge(start, end);

		// Checks it can be retrieved by id.
		assertTrue(graph.getEdge(newEdge.getId()).equals(newEdge));
	}

	@Test
	public void createPersistentEdge() {

		// Creates two vertices and edge between them.
		IVertex start = graph.createVertex();
		IVertex end = graph.createVertex();
		IEdge newEdge = graph.createEdge(start, end);
		graph.commit();
		graph.close();

		// Reopens the graph, checks that the edge is there.
		graph = createGraph();
		assertTrue(graph.getEdge(newEdge.getId()).equals(newEdge));
	}

}