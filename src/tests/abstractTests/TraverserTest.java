package tests.abstractTests;

import static org.junit.Assert.assertEquals;
import graphInterfaces.IEdge;
import graphInterfaces.IIndex;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class TraverserTest<V extends IVertex, E extends IEdge> {

	private IPersistentGraph<V, E> graph;
	
	public abstract IPersistentGraph<V, E> createGraph(String name);
	
	@Before
	public void setup() {
		
		// Initialise the graph and operator.
		graph = createGraph("neighbour test");
		graph.clear();
		
		/*
		 * Build graph with vertices 0, 1, ..., 8 and
		 * edges {0,1}, {0,4}, {2,8}, {5,1}, {5,2}, {5,4}, {5,8}, {6,0}, {6,5}, {7,5}
		 */
		
		// Adds and indexes vertices with a number.
		int nodeCount = 9;
		V vertex;
		IIndex<V> index = graph.index().forVertices("vertices");
		for (Integer i = 0; i < nodeCount; i++) {
			vertex = graph.createVertex();
			vertex.setProperty("number", i.toString());
			index.add(vertex, "number", i.toString());
		}
		
		// Adds edges.
		int edges[][] = {{0,1}, {0,4}, {2,8}, {5,1}, {5,2}, {5,4}, {5,8}, {6,0}, {6,5}, {7,5}};
		for (int i = 0; i < edges.length; i++) {
			graph.createEdge(getVertex(edges[i][0]), getVertex(edges[i][1]), "connects to");
		}
		graph.createEdge(getVertex(5), getVertex(3), "ignore");
		graph.createEdge(getVertex(0), getVertex(3), "ignore");
		graph.createEdge(getVertex(3), getVertex(7), "ignore");
		graph.commit();
	}
	
	@After
	public void cleanup() {
		graph.close();
	}
	
	@Test
	public void traverseToDepth0() {
		
		// Traversing at depth 0, traverses only the vertex itself.
		ArrayList<String> connections = new ArrayList<String>();
		connections.add("connects to");
		int i = 0;
		for (V v : graph.getOperator().createTraverser(0, 0, connections, Direction.BOTH).traverse(getVertex(0))) {
			assertEquals(getVertex(0), v);
			i++;
		}
		assertEquals(1, i);
	}
	
	@Test
	public void traverseToDepth3() {
		ArrayList<String> connections = new ArrayList<String>();
		connections.add("connects to");
		int traversalOrder[] = {0, 1, 4, 6, 5, 2, 8, 7}; // should traverse in this order.
		int i = 0;
		for (V v :graph.getOperator().createTraverser(0, 3, connections, Direction.BOTH).traverse(getVertex(0))) {
			// System.out.println(i + " " + traversalOrder[i] + " " + v.getProperty("number"));
			assertEquals(getVertex(traversalOrder[i]), v);
			i++;
		}
		assertEquals(traversalOrder.length, i);
	}
	
	@Test
	public void traverseOutgoing() {
		ArrayList<String> connections = new ArrayList<String>();
		connections.add("connects to");
		int traversalOrder[] = {5, 1, 2, 4, 8}; // should traverse in this order.
		int i = 0;
		for (V v :graph.getOperator().createTraverser(0, 2, connections, Direction.OUTGOING).traverse(getVertex(5))) {
			// System.out.println(i + " " + traversalOrder[i] + " " + v.getProperty("number"));
			assertEquals(getVertex(traversalOrder[i]), v);
			i++;
		}
		assertEquals(traversalOrder.length, i);
	}
	
	@Test
	public void traverseIncoming() {
		ArrayList<String> connections = new ArrayList<String>();
		connections.add("connects to");
		int traversalOrder[] = {2, 5, 6, 7}; // should traverse in this order.
		int i = 0;
		for (V v :graph.getOperator().createTraverser(0, 2, connections, Direction.INCOMING).traverse(getVertex(2))) {
			// System.out.println(i + " " + traversalOrder[i] + " " + v.getProperty("number"));
			assertEquals(getVertex(traversalOrder[i]), v);
			i++;
		}
		assertEquals(traversalOrder.length, i);
	}
	
	@Test
	public void traverseIgnores() {
		ArrayList<String> empty = new ArrayList<String>();
		int traversalOrder[] = {7, 5, 3, 1, 2, 4, 8, 6, 0}; // should traverse in this order.
		int i = 0;
		for (V v :graph.getOperator().createTraverser(0, 3, empty, Direction.BOTH).traverse(getVertex(7))) {
			// System.out.println(i + " " + traversalOrder[i] + " " + v.getProperty("number"));
			assertEquals(getVertex(traversalOrder[i]), v);
			i++;
		}
		assertEquals(traversalOrder.length, i);
	}
	
	/**
	 * 
	 * Gets vertex by its name. The name is "vertex" + its number.
	 * 
	 * @return
	 */
	private V getVertex(Integer number) {
		for (V v :graph.index().forVertices("vertices").get("number", number.toString())) {
			return v;
		}
		
		return null;
	}

}
