package neo4jGraph;

import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class Neo4jEdge implements IEdge {

	/**
	 * Property for different types of edges, corresponds to the Relationship type in Neo4j.
	 */
	public static final String EDGE_TYPE = "EdgeType";

	private Relationship edge;
	private Neo4jVertex start;
	private Neo4jVertex end;
	private Neo4jGraph graph;

	protected Neo4jEdge(Relationship edge, Neo4jGraph graph) {
		this.edge = edge;
		this.graph = graph;
		start = new Neo4jVertex(edge.getStartNode(), graph);
		end = new Neo4jVertex(edge.getEndNode(), graph);
	}

	@Override
	public void setProperty(String key, String value) {
		
		graph.ensureInTransaction();

		// If the property is the type of the edge,
		if (key.equals(EDGE_TYPE)) {

			// creates a new relationship with that type,
			Node startNode = ((Neo4jVertex) start).getNode();
			Node endNode = ((Neo4jVertex) end).getNode();
			Relationship newEdge = startNode.createRelationshipTo(endNode, DynamicRelationshipType.withName(value));

			// copies all properties,
			for (String pKey : edge.getPropertyKeys()) {
				newEdge.setProperty(pKey, edge.getProperty(pKey));
			}

			// deletes and replaces the old edge.
			edge.delete();
			edge = newEdge;

		} else {
			edge.setProperty(key, value);
		}
	}

	@Override
	public Iterable<String> getKeys() {
		return edge.getPropertyKeys();
	}

	@Override
	public String getProperty(String key) {

		// If the property is the type of the edge returns the name of the relationship type.
		if (key.equals(EDGE_TYPE)) {
			return edge.getType().name();
		}

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
