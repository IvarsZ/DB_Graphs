package neo4jGraph;

import graphInterfaces.IIndex;
import graphInterfaces.IIndexManager;

public class Neo4jIndexManager implements IIndexManager<Neo4jVertex, Neo4jEdge> {
	
	private Neo4jGraph graph;
	
	protected Neo4jIndexManager(Neo4jGraph graph) {
		this.graph = graph;
	}

	@Override
	public IIndex<Neo4jVertex> forVertices(String indexName) {
		return new Neo4jVertexIndex(graph.getNeo4jDatabase().index().forNodes(indexName), graph);
	}

	@Override
	public IIndex<Neo4jEdge> forEdges(String indexName) {
		return new Neo4jEdgeIndex(graph.getNeo4jDatabase().index().forRelationships(indexName), graph);
	}

}
