package neo4jGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

import org.neo4j.graphdb.Relationship;

public class Neo4jEdge implements IEdge {
	
	Relationship edge;
	Neo4jVertex start;
	Neo4jVertex end;
	
	protected Neo4jEdge(Relationship edge) {
		this.edge = edge;
		start = new Neo4jVertex(edge.getStartNode());
		end = new Neo4jVertex(edge.getEndNode());
	}

	@Override
	public void setProperty(String key, String value) {
		edge.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return edge.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {
		return (String) edge.getProperty(key);
	}

	@Override
	public IVertex getStart() {
		return start;
	}

	@Override
	public IVertex getEnd() {
		return end;
	}

	@Override
	public long getId() {
		
		return edge.getId();
	}

}
