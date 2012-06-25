package tests.neo4jGraph;

import tests.IndexTest;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;
import graphInterfaces.IPersistentGraph;

public class Neo4jIndexTest extends IndexTest<Neo4jVertex, Neo4jEdge> {

	private static final String path = "neo4j-index-test";

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph() {
		return new Neo4jGraph(path);
	}
}