package graphFactories;

import graphInterfaces.IPersistentGraph;
import neo4jGraph.Neo4jEdge;
import neo4jGraph.Neo4jGraph;
import neo4jGraph.Neo4jVertex;

public class Neo4jFactory implements IFactory<Neo4jVertex, Neo4jEdge> {

	@Override
	public IPersistentGraph<Neo4jVertex, Neo4jEdge> createGraph(String name) {
		
		return new Neo4jGraph(System.getProperty("user.home") + "/graphs/" + name);
	}
	
	@Override
	public String getPrintDetails() {
		return "neo4j";
	}

}
