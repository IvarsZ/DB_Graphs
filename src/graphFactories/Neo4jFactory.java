package graphFactories;

import graphInterfaces.IPersistentGraph;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;

public class Neo4jFactory implements IFactory<Neo4jVertex, Neo4jEdge> {

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createEmptyGraph(String name) {
		
		IPersistentGraph<Neo4jVertex, Neo4jEdge> graph = new Neo4jGraph(name);
		graph.clear();
		return graph;
	}

}
