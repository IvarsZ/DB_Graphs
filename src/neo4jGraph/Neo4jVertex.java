package neo4jGraph;

import graphInterfaces.IVertex;

import org.neo4j.graphdb.Node;

public class Neo4jVertex implements IVertex {
	
	Node node;
	
	protected Neo4jVertex(Node node) {
		this.node = node;
	}

	@Override
	public void setProperty(String key, String value) {
		node.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return node.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {
		return (String) node.getProperty(key);
	}

	@Override
	public long getId() {
		
		return node.getId();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		// Two Neo4jVertices are equal, if their nodes have equal id's and belong to the same database.
		if (obj instanceof Neo4jVertex) {
			Node node = ((Neo4jVertex) obj).node;
			
			// TODO : test graph db equality.
			return this.node.getId() == node.getId() && this.node.getGraphDatabase().equals(node.getGraphDatabase());
		}
		
		return false;
	}

}
