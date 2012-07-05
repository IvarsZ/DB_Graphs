package neo4jGraph;

import graphInterfaces.IVertex;

import org.neo4j.graphdb.Node;

public class Neo4jVertex implements IVertex {
	
	private Node vertex;
	private Neo4jGraph graph;
	
	protected Neo4jVertex(Node node, Neo4jGraph graph) {
		this.vertex = node;
		this.graph = graph;
	}

	@Override
	public void setProperty(String key, String value) {
		graph.ensureInTransaction();
		vertex.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return vertex.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {
		return (String) vertex.getProperty(key, null);
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
	
	@Override
	public int hashCode() {
		return (int) vertex.getId();
	}
	
	protected Node getNode() {
		return vertex;
	}

}
