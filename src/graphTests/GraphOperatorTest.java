package graphTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import graphInterfaces.IEdge;
import graphInterfaces.IGraphOperator;
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
	private ArrayList<V> vertices;
	
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
		
		// Adds and saves vertices.
		int nodeCount = 9;
		vertices = new ArrayList<V>();
		for (int i = 0; i < nodeCount; i++) {
			vertices.add(graph.createVertex());
		}
		
		// Adds edges.
		int edges[][] = {{0,1}, {0,4}, {2,8}, {5,1}, {5,2}, {5,4}, {5,8}, {6,0}, {6,5}, {7,5}};
		for (int i = 0; i < edges.length; i++) {
			graph.createEdge(vertices.get(edges[i][0]), vertices.get(edges[i][1]));
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
		assertTrue(operator.findNeighbours(vertices.get(3)).isEmpty());
	}
	
	@Test
	public void oneNeighbour() {
		
		// Node7 has one neighbour Node5.
		Set<V> neighbours = operator.findNeighbours(vertices.get(7));
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(5)));
	}
	
	@Test
	public void onlyOutGoingNeighbours() {
		
		// Node6 has 2 neighbours (0 and 5) both are outgoing.
		Set<V> neighbours = operator.findNeighbours(vertices.get(6));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(0)));
		assertTrue(neighbours.contains(vertices.get(5)));
	}
	
	@Test
	public void onlyIngoingNeighbours() {
		
		// Node8 has 2 neighbours (2 and 5) both are ingoing.
		Set<V> neighbours = operator.findNeighbours(vertices.get(8));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(2)));
		assertTrue(neighbours.contains(vertices.get(5)));
	}
	
	@Test
	public void multipleNeighbours() {
		
		// Node 5 has 6 neighbours 1, 2, 4, 6, 7, 8.
		Set<V> neighbours = operator.findNeighbours(vertices.get(5));
		assertEquals(6, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(1)));
		assertTrue(neighbours.contains(vertices.get(2)));
		assertTrue(neighbours.contains(vertices.get(4)));
		assertTrue(neighbours.contains(vertices.get(6)));
		assertTrue(neighbours.contains(vertices.get(7)));
		assertTrue(neighbours.contains(vertices.get(8)));
	}
	
	// At depth tester
	@Test
	public void noNeighboursAtDepth2() {
		
		// Node3 has no neighbours at depth 2.
		assertTrue(operator.findNeighbours(vertices.get(3), 2).isEmpty());
	}
	
	@Test
	public void oneNeighbourAtDepth3() {
		
		// Node7 has one neighbour at depth 3 - Node5.
		Set<V> neighbours = operator.findNeighbours(vertices.get(7), 3);
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(0)));
	}
	
	@Test
	public void multipleNeighboursAtDepth3() {
		
		// Node 0 has 3 neighbours at depth 3 - 2, 7, 8.
		Set<V> neighbours = operator.findNeighbours(vertices.get(0), 3);
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(vertices.get(2)));
		assertTrue(neighbours.contains(vertices.get(7)));
		assertTrue(neighbours.contains(vertices.get(8)));
	}
	
	@Test
	public void findNeigboursAtDepth3ForLargeGraph() {
		
		// Writes/creates the graph used in testing. 
		// TODO : implement.
	}
}
