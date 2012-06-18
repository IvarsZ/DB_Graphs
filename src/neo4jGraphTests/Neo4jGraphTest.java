package neo4jGraphTests;

import graphInterfaces.IPersistentGraph;
import graphTests.PersistentGraphTest;
import neo4jGraph.Neo4jGraph;

public class Neo4jGraphTest extends PersistentGraphTest {

	private static final String path = "neo4j-graph-test";

	@Override
	public IPersistentGraph createGraph() {

		// Creates a graph.
		return new Neo4jGraph(path);
	}
}
