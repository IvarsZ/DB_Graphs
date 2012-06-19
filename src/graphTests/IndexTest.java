package graphTests;

import static org.junit.Assert.assertTrue;
import graphInterfaces.IEdge;
import graphInterfaces.IIndex;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class IndexTest<V extends IVertex, E extends IEdge> {

	/**
	 * 
	 * Might just reopen, not guaranteed to be empty.
	 * 
	 * @return
	 */
	public abstract IPersistentGraph<V, E> createGraph();

	private IPersistentGraph<V, E> graph;

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
	public void indexVertex() {

		// Adds a vertex with a name, and indexes it by the name.
		V vertex = graph.createVertex();
		vertex.setProperty("name", "bob");
		IIndex<V> index = graph.index().forVertices("named vertices");
		index.add(vertex, "name", vertex.getProperty("name"));
		graph.commit();

		// Gets and checks the vertex by its indexed name.
		assertTrue(containsOnly(index.get("name", "bob"), vertex));
	}

	@Test
	public void indexTwoVertices() {

		// Adds 2 vertices one with name and other with occupation, and indexes them.
		V v1 = graph.createVertex();
		v1.setProperty("name", "bob");
		V v2 = graph.createVertex();
		v2.setProperty("occupation", "student");
		IIndex<V> index = graph.index().forVertices("vertices");
		index.add(v1, "name", v1.getProperty("name"));
		index.add(v2, "occupation", v2.getProperty("occupation"));
		graph.commit();

		// Gets and checks the vertices by their indexed names.
		assertTrue(containsOnly(index.get("name", "bob"), v1));
		assertTrue(containsOnly(index.get("occupation", "student"), v2));
	}

	@Test
	public void indexVerticesWithOnePair() {

		// Adds 2 vertices and indexes them with the same key-value pair.
		V v1 = graph.createVertex();
		V v2 = graph.createVertex();
		IIndex<V> index = graph.index().forVertices("vertices");
		index.add(v1, "name", "equalnames");
		index.add(v2, "name", "equalnames");
		graph.commit();

		Iterable<V> vertices = index.get("name", "equalnames");
		assertTrue(contains(vertices, v1));
		assertTrue(contains(vertices, v1));
	}

	@Test
	public void indexVertexWithTwoPairs() {

		// Adds a vertex, and indexes it by name and age pair.
		V vertex = graph.createVertex();
		IIndex<V> index = graph.index().forVertices("named vertices");
		index.add(vertex, "name", "bob");
		index.add(vertex, "age", "21");
		graph.commit();

		// Gets and checks the vertex by the name and age.
		assertTrue(containsOnly(index.get("name", "bob"), vertex));
		assertTrue(containsOnly(index.get("age", "21"), vertex));
	}

	@Test
	public void indexEdge() {

		// Adds an edge with a name, and indexes it by the name.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E edge = graph.createEdge(start, end);
		edge.setProperty("name", "john");
		IIndex<E> index = graph.index().forEdges("named edges");
		index.add(edge, "name", edge.getProperty("name"));
		graph.commit();

		// Gets and checks the vertex by its indexed name.
		assertTrue(containsOnly(index.get("name", "john"), edge));

	}

	@Test
	public void indexTwoEdges() {

		// Adds 2 edges one with name and other with age, and indexes them.
		V start1 = graph.createVertex();
		V end1 = graph.createVertex();
		E e1 = graph.createEdge(start1, end1);
		e1.setProperty("name", "john");

		V start2 = graph.createVertex();
		V end2 = graph.createVertex();
		E e2 = graph.createEdge(start2, end2);
		e2.setProperty("age", "13");

		IIndex<E> index = graph.index().forEdges("named edges");
		index.add(e1, "name", e1.getProperty("name"));
		index.add(e2, "age", e2.getProperty("age"));
		graph.commit();

		// Gets and checks the vertex by its indexed name.
		assertTrue(containsOnly(index.get("name", "john"), e1));
		assertTrue(containsOnly(index.get("age", "13"), e2));
	}

	@Test
	public void indexTwoEdgesWithOnePair() {

		// Adds 2 edges and indexes them with the same key-value pair.
		V start1 = graph.createVertex();
		V end1 = graph.createVertex();
		E e1 = graph.createEdge(start1, end1);

		V start2 = graph.createVertex();
		V end2 = graph.createVertex();
		E e2 = graph.createEdge(start2, end2);

		IIndex<E> index = graph.index().forEdges("named edges");
		index.add(e1, "name", "equalnames");
		index.add(e2, "name", "equalnames");
		graph.commit();


		Iterable<E> vertices = index.get("name", "equalnames");
		assertTrue(contains(vertices, e1));
		assertTrue(contains(vertices, e2));
	}
	
	@Test
	public void indexEdgeWithTwoPairs() {

		// Adds an edge, and indexes it by name and age.
		V start = graph.createVertex();
		V end = graph.createVertex();
		E edge = graph.createEdge(start, end);
		IIndex<E> index = graph.index().forEdges("named edges");
		index.add(edge, "name", "harry");
		index.add(edge, "age", "23");
		graph.commit();

		// Gets and checks the vertex by the name and age.
		assertTrue(containsOnly(index.get("name", "harry"), edge));
		assertTrue(containsOnly(index.get("age", "23"), edge));

	}

	private static <F> boolean containsOnly(Iterable<F> entities, F entity) {

		int count = 0;
		for (F f : entities) {
			if (f.equals(entity) == false) {
				return false;
			}
			count++;
		}

		return count == 1;
	}

	private static <F> boolean contains(Iterable<F> entities, F entity) {

		for (F f : entities) {
			if (f.equals(entity) == true) {
				return true;
			}
		}

		return false;
	}

}
