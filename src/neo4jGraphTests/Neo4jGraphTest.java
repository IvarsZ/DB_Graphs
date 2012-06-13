package neo4jGraphTests;

import neo4jGraph.Neo4jGraph;
import graphInterfaces.IGraph;
import graphTests.GraphTest;

public class Neo4jGraphTest extends GraphTest {
	
	private static final String path = "neo4j-graph-test";

	@Override
	public IGraph createEmptyGraph() {
		
		// Creates a graph an clears it.
		IGraph graph = new Neo4jGraph(path);
		graph.clear();
		return graph;
	}

}
