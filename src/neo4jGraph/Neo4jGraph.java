package neo4jGraph;

import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

public class Neo4jGraph implements IGraph {
	
	private GraphDatabaseService graphDb;
	
	// TODO : constructor.

	@Override
	public IVertex createVertex() throws IllegalArgumentException {
		
		return new Neo4jVertex(graphDb.createNode());
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException {
		
		// TODO: solve incompatible vertices problem (not in the graph), node equality problematic in embedded db's.
		return null;
	}

	@Override
	public IVertex getVertex(int id) {
		return new Neo4jVertex(graphDb.getNodeById(id));
	}

	@Override
	public IEdge getEdge(int id) {
		return new Neo4jEdge(graphDb.getRelationshipById(id));
	}

	@Override
	public Set<IVertex> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IEdge> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
