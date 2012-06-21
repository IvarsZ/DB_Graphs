package neo4jGraphTests;

import graphInterfaces.IPersistentGraph;
import graphTests.GraphOperatorTest;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;

public class Neo4jGraphOperatorTest extends GraphOperatorTest<Neo4jVertex, Neo4jEdge> {

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph(String name) {
		return new Neo4jGraph("neo4j" + name);
	}

}
