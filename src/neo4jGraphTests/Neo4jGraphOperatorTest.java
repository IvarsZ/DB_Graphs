package neo4jGraphTests;

import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jGraphOperator;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphTests.GraphOperatorTest;

public class Neo4jGraphOperatorTest extends GraphOperatorTest {
	
	private static final String path = "neo4j-graph-operator-test";

	@Override
	public IPersistentGraph createGraph() {
		return new Neo4jGraph(path);
	}

	@Override
	public IGraphOperator createOperator(IPersistentGraph graph) {
		return new Neo4jGraphOperator(graph);
	}

}
