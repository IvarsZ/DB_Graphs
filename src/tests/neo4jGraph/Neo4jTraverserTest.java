package tests.neo4jGraph;

import graphInterfaces.IPersistentGraph;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;
import tests.abstractTests.TraverserTest;

public class Neo4jTraverserTest extends TraverserTest<Neo4jVertex, Neo4jEdge> {

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph(String name) {
		return new Neo4jGraph("neo4j" + name);
	}

}
