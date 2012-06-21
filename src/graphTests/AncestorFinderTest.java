package graphTests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import neo4jGraph.Neo4jEdge;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;

import graphInterfaces.IEdge;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IIndex;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public abstract class AncestorFinderTest<V extends IVertex, E extends IEdge> {

	private IPersistentGraph<V, E> graph;

	public abstract IPersistentGraph<V, E> createGraph(String name);

	@Before
	public void setup() {

		// Initialise the graph and operator.
		graph = createGraph("neighbour test");
		graph.clear();

		// Adds and indexes vertices with a number.
		int nodeCount = 16;
		V vertex;
		IIndex<V> index = graph.index().forVertices("vertices");
		for (Integer i = 1; i <= nodeCount; i++) {
			vertex = graph.createVertex();
			index.add(vertex, "number", i.toString());
		}

		// Adds edges.
		int edges[][] = { {1,4}, {2,4}, {2,5}, {3,6}, {4,6}, {4,7}, {4,8}, {3,8}, {3,7},
						  {4,9}, {3,9}, {5,9}, {6,10}, {6,11}, {7,10}, {7,11}, {7,12},
						  {9,13}, {9,14}, {12,16}, {15,16} };
		
		E edge;
		for (int i = 0; i < edges.length; i++) {
			edge = graph.createEdge(getVertex(edges[i][0]), getVertex(edges[i][1]));
			
			// TODO : fix relationship types.
			edge.setProperty(Neo4jEdge.EDGE_TYPE, "parent of");
		}
		graph.commit();

	}
	
	@After
	public void cleanup() {
		graph.close();
	}
	
	@Test
	public void test() {
		
		// TODO : fix multiple ancestors.
		// The lowest common ancestor of vertex 11 and vertex 9 is vertex 4 or vertex 3...
		V v11 = getVertex(11);
		V v9 = getVertex(9);
		V v4 = getVertex(4);
		V v3 = getVertex(3);
		
		ArrayList<String> edgeTypes = new ArrayList<String>();
		edgeTypes.add("parent of");
		V ancestor = graph.getOperator().findAncestor(v11, v9, 5, edgeTypes, IGraphOperator.Direction.INCOMING);
		
		// assertEquals(v4, ancestor);
		assertTrue(ancestor.equals(v3) || ancestor.equals(v4));
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
