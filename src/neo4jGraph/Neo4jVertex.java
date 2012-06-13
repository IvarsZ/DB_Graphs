package neo4jGraph;

import graphInterfaces.IVertex;

import org.neo4j.graphdb.Node;

public class Neo4jVertex implements IVertex {
	
	private Node vertex;
	
	protected Neo4jVertex(Node node) {
		this.vertex = node;
	}

	@Override
	public void setProperty(String key, String value) {
		vertex.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return vertex.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {
		return (String) vertex.getProperty(key);
	}

	@Override
	public long getId() {
		
		return vertex.getId();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		// Two Neo4jVertices are equal, if their nodes have equal id's and belong to the same database.
		if (obj instanceof Neo4jVertex) {
			Node vertex = ((Neo4jVertex) obj).vertex;
			
			return this.vertex.getId() == vertex.getId() && this.vertex.getGraphDatabase().equals(vertex.getGraphDatabase());
		}
		
		return false;
	}
	
	protected Node getNode() {
		return vertex;
	}

}
