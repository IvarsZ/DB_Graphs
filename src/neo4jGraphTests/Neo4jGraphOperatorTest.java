package neo4jGraphTests;

import graphInterfaces.IPersistentGraph;
import graphTests.GraphOperatorTest;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;

public class Neo4jGraphOperatorTest extends GraphOperatorTest<Neo4jVertex, Neo4jEdge> {
	
	private static final String path = "neo4j-graph-operator-test";

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph() {
		return new Neo4jGraph(path);
	}

}
