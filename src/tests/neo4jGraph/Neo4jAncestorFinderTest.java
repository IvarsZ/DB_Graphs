package tests.neo4jGraph;

import tests.AncestorFinderTest;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;
import graphInterfaces.IPersistentGraph;

public class Neo4jAncestorFinderTest extends AncestorFinderTest<Neo4jVertex, Neo4jEdge> {

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph(String name) {
		return new Neo4jGraph("neo4j" + name);
	}

}
