package neo4jGraph;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.neo4j.graphdb.Relationship;

public class Neo4jEdgeIterator implements Iterator<Neo4jEdge> {

	Iterator<Relationship> edges;
	Neo4jGraph graph;

	protected Neo4jEdgeIterator(Iterator<Relationship> edges, Neo4jGraph graph) {
		this.edges = edges;
		this.graph = graph;
	}

	@Override
	public boolean hasNext() {
		return edges.hasNext();
	}

	@Override
	public Neo4jEdge next() {
		
		if (edges.hasNext()) {
			return new Neo4jEdge(edges.next(), graph);
		}
		else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		edges.remove();
	}

}
