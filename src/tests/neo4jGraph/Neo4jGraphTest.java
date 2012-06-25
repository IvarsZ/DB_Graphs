package tests.neo4jGraph;

import tests.PersistentGraphTest;
import graphInterfaces.IPersistentGraph;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;

public class Neo4jGraphTest extends PersistentGraphTest<Neo4jVertex, Neo4jEdge> {

	private static final String path = "neo4j-graph-test";

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph() {

		// Creates a graph.
		return new Neo4jGraph(path);
	}
}
