package neo4jGraph;

import graphInterfaces.IIndex;

import java.util.Iterator;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

/**
 * 
 * Neo4j implementation of IIndex for edges.
 * 
 * @author iz2
 *
 */
public class Neo4jEdgeIndex implements IIndex<Neo4jEdge> {
	
	private Index<Relationship> index;
	private Neo4jGraph graph;

	protected Neo4jEdgeIndex(Index<Relationship> index, Neo4jGraph graph) {
		this.index = index;
		this.graph = graph;
	}

	@Override
	public void add(Neo4jEdge edge, String key, String value) {
		graph.ensureInTransaction();
		index.add(edge.getRelationship(), key, value);
	}

	@Override
	public Iterable<Neo4jEdge> get(final String key, final String value) {
		
		return new Iterable<Neo4jEdge>() {

			@Override
			public Iterator<Neo4jEdge> iterator() {
				return new Neo4jEdgeIterator(index.get(key, value), graph);
			}
			
		};
	}

	@Override
	public Neo4jEdge getFirst(String key, String value) {
		
		// If at least one relationship is gotten from the index returns new neo4j edge with it,
		Relationship firstNode = index.get(key, value).getSingle();
		if (firstNode != null) {
			return new Neo4jEdge(index.get(key, value).getSingle(), graph);
		}
		
		// otherwise null.
		return null;
	}

}
