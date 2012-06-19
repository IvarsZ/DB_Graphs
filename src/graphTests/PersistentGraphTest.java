package graphTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class PersistentGraphTest<V extends IVertex, E extends IEdge> {

	private IPersistentGraph<V, E> graph;

	/**
	 * 
	 * Might just reopen, not guaranteed to be empty.
	 * 
	 * @return
	 */
	public abstract IPersistentGraph<V, E> createGraph();

	@Before
	public void setup(){

		// Creates empty graph.
		graph = createGraph();
		graph.clear();
	}

	@After
	public void cleanup() {

		// Clears the graph and closes it.
		graph.clear();
		graph.close();
	}

	@Test
	public void createEmptyGraphTest() {

		// Checks that the graph is empty.
		for (V v : graph.getVertices()) {
			System.out.println(v.getId());
			fail("empty graph has a vertex " + v + " " + v.getId());
		}

		// Checks that the graph is empty.
		for (E e : graph.getEdges()) {
			fail("empty graph has an edge " + e);
		}
	}

	@Test
	public void createVertex() {

		// Creates a vertex.
		V newVertex = graph.createVertex();

		// Checks it can be retrieved by id.
		assertTrue(graph.getVertex(newVertex.getId()).equals(newVertex));
	}

	@Test
	public void createPersistentVertex() {

		// Creates a vertex, and closes the graph.
		V newVertex = graph.createVertex();
		graph.commit();
		graph.close();

		// Reopens the graph, checks that the vertex is there.
		graph = createGraph();
		assertTrue(graph.getVertex(newVertex.getId()).equals(newVertex));
	}

	@Test
	public void rollbackCreatedVertex() {

		// Creates a vertex, and rolls back the changes.
		V newVertex = graph.createVertex();
		graph.rollback();

		// Checks that the vertex isn't there.
		assertEquals(null, graph.getVertex(newVertex.getId()));
	}

	@Test
	public void createEdge() {

		// Creates two vertices and edge between them.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E newEdge = graph.createEdge(start, end);

		// Checks it can be retrieved by id.
		assertTrue(graph.getEdge(newEdge.getId()).equals(newEdge));
	}

	@Test
	public void createPersistentEdge() {

		// Creates two vertices and edge between them.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E newEdge = graph.createEdge(start, end);
		graph.commit();
		graph.close();

		// Reopens the graph, checks that the edge is there.
		graph = createGraph();
		assertTrue(graph.getEdge(newEdge.getId()).equals(newEdge));
	}

	@Test
	public void rollbackCreatedEdge() {

		// Creates two vertices and edge between them, then rolls back the changes.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E newEdge = graph.createEdge(start, end);
		graph.rollback();

		// Checks that the edge isn't there.
		assertEquals(null, graph.getEdge(newEdge.getId()));
	}

	@Test
	public void createVertexWithProperties() {

		// Creates a vertex with name and age, and closes the graph.
		V newVertex = graph.createVertex();
		newVertex.setProperty("name", "test node 1");
		newVertex.setProperty("age", "20");
		graph.commit();
		graph.close();

		// Reopens the graph, checks the properties.
		graph = createGraph();
		newVertex = graph.getVertex(newVertex.getId());
		assertEquals("test node 1", newVertex.getProperty("name"));
		assertEquals("20", newVertex.getProperty("age"));
	}
	
	@Test
	public void createEdgeWithProperties() {

		// Creates an edge with type and age, and closes the graph.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E newEdge = graph.createEdge(start, end);
		newEdge.setProperty("EdgeType", "Knows");
		newEdge.setProperty("age", "2");
		graph.commit();
		graph.close();

		// Reopens the graph, checks the properties.
		graph = createGraph();
		newEdge = graph.getEdge(newEdge.getId());
		assertEquals("Knows", newEdge.getProperty("EdgeType"));
		assertEquals("2", newEdge.getProperty("age"));
	}

}
