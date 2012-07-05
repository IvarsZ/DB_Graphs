package neo4jGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

import org.neo4j.graphdb.Relationship;

public class Neo4jEdge implements IEdge {

	private Relationship edge;
	private Neo4jVertex start;
	private Neo4jVertex end;
	private Neo4jGraph graph;

	protected Neo4jEdge(Relationship edge, Neo4jGraph graph) {
		
		if (edge == null) {
			throw new IllegalArgumentException("null edge");
		}
		
		if (graph == null) {
			throw new IllegalArgumentException("null graph");
		}
		
		this.edge = edge;
		this.graph = graph;
		start = new Neo4jVertex(edge.getStartNode(), graph);
		end = new Neo4jVertex(edge.getEndNode(), graph);
	}

	@Override
	public void setProperty(String key, String value) {

		graph.ensureInTransaction();
		edge.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return edge.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {
		return (String) edge.getProperty(key, null);
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

	@Override
	public String getType() {
		return edge.getType().name();
	}

	@Override
	public boolean equals(Object obj) {

		// Two Neo4jEdges are equal, if their relationships have equal id's and belong to the same database.
		if (obj instanceof Neo4jEdge) {
			Relationship edge = ((Neo4jEdge) obj).edge;

			return this.edge.getId() == edge.getId() && this.edge.getGraphDatabase().equals(edge.getGraphDatabase());
		}

		return false;
	}

	protected Relationship getRelationship() {
		return edge;
	}
}
