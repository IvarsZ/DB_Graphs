package neo4jGraph;

import java.util.Iterator;

import graphInterfaces.IIndex;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class Neo4jVertexIndex implements IIndex<Neo4jVertex> {
	
	private Index<Node> index;
	private Neo4jGraph graph;
	
	

	protected Neo4jVertexIndex(Index<Node> index, Neo4jGraph graph) {
		this.index = index;
		this.graph = graph;
	}

	@Override
	public void add(Neo4jVertex vertex, String key, String value) {
		graph.ensureInTransaction();
		index.add(vertex.getNode(), key, value);
	}

	@Override
	public Iterable<Neo4jVertex> get(final String key, final String value) {
		return new Iterable<Neo4jVertex>() {

			@Override
			public Iterator<Neo4jVertex> iterator() {
				return new Neo4jVertexIterator(index.get(key, value), graph);
			}
			
		};
	}
}
