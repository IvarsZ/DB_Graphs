package graphTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import graphInterfaces.IEdge;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IIndex;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class GraphOperatorTest<V extends IVertex, E extends IEdge> {

	private IPersistentGraph<V, E> graph;
	private IGraphOperator<V, E> operator;
	
	public abstract IPersistentGraph<V, E> createGraph();
	
	@Before
	public void setup() {
		
		// Initialise the graph and operator.
		graph = createGraph();
		graph.clear();
		
		operator = graph.getOperator();
		
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
			index.add(vertex, "number", i.toString());
		}
		
		// Adds edges.
		int edges[][] = {{0,1}, {0,4}, {2,8}, {5,1}, {5,2}, {5,4}, {5,8}, {6,0}, {6,5}, {7,5}};
		for (int i = 0; i < edges.length; i++) {
			graph.createEdge(getVertex(edges[i][0]), getVertex(edges[i][1]));
		}
		graph.commit();
	}
	
	@After
	public void cleanup() {
		graph.close();
	}
	
	@Test
	public void noNeighbours() {
		
		// Node3 has no neighbours.
		assertTrue(operator.findNeighbours(getVertex(3)).isEmpty());
	}
	
	@Test
	public void oneNeighbour() {
		
		// Node7 has one neighbour Node5.
		Set<V> neighbours = operator.findNeighbours(getVertex(7));
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(getVertex(5)));
	}
	
	@Test
	public void onlyOutGoingNeighbours() {
		
		// Node6 has 2 neighbours (0 and 5) both are outgoing.
		Set<V> neighbours = operator.findNeighbours(getVertex(6));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(getVertex(0)));
		assertTrue(neighbours.contains(getVertex(5)));
	}
	
	@Test
	public void onlyIngoingNeighbours() {
		
		// Node8 has 2 neighbours (2 and 5) both are ingoing.
		Set<V> neighbours = operator.findNeighbours(getVertex(8));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(getVertex(2)));
		assertTrue(neighbours.contains(getVertex(5)));
	}
	
	@Test
	public void multipleNeighbours() {
		
		// Node 5 has 6 neighbours 1, 2, 4, 6, 7, 8.
		Set<V> neighbours = operator.findNeighbours(getVertex(5));
		assertEquals(6, neighbours.size());
		assertTrue(neighbours.contains(getVertex(1)));
		assertTrue(neighbours.contains(getVertex(2)));
		assertTrue(neighbours.contains(getVertex(4)));
		assertTrue(neighbours.contains(getVertex(6)));
		assertTrue(neighbours.contains(getVertex(7)));
		assertTrue(neighbours.contains(getVertex(8)));
	}
	
	// At depth tester
	@Test
	public void noNeighboursAtDepth2() {
		
		// Node3 has no neighbours at depth 2.
		assertTrue(operator.findNeighbours(getVertex(3), 2).isEmpty());
	}
	
	@Test
	public void oneNeighbourAtDepth3() {
		
		// Node7 has one neighbour at depth 3 - Node5.
		Set<V> neighbours = operator.findNeighbours(getVertex(7), 3);
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(getVertex(0)));
	}
	
	@Test
	public void multipleNeighboursAtDepth3() {
		
		// Node 0 has 3 neighbours at depth 3 - 2, 7, 8.
		Set<V> neighbours = operator.findNeighbours(getVertex(0), 3);
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(getVertex(2)));
		assertTrue(neighbours.contains(getVertex(7)));
		assertTrue(neighbours.contains(getVertex(8)));
	}
	
	@Test
	public void findNeigboursAtDepth3ForLargeGraph() {
		
		// Writes/creates the graph used in testing. 
		// TODO : implement.
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
