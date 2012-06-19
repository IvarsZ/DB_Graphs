package neo4jGraph;

import java.util.Iterator;

import graphInterfaces.IIndex;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

public class Neo4jEdgeIndex implements IIndex<Neo4jEdge> {
	
	private Index<Relationship> index;
	private Neo4jGraph graph;

	@Override
	public void add(Neo4jEdge edge, String key, String value) {
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

}
