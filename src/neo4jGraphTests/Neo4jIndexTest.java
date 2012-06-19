package neo4jGraphTests;

import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;
import graphInterfaces.IPersistentGraph;
import graphTests.IndexTest;

public class Neo4jIndexTest extends IndexTest<Neo4jVertex, Neo4jEdge> {

	private static final String path = "neo4j-index-test";

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph() {
		return new Neo4jGraph(path);
	}
}