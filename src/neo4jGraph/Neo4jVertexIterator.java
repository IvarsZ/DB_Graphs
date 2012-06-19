package neo4jGraph;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.neo4j.graphdb.Node;

public class Neo4jVertexIterator implements Iterator<Neo4jVertex> {

	Iterator<Node> nodes;
	Neo4jGraph graph;

	public Neo4jVertexIterator(Iterator<Node> nodes, Neo4jGraph graph) {
		this.nodes = nodes;
		this.graph = graph;
	}

	@Override
	public boolean hasNext() {
		return nodes.hasNext();
	}

	@Override
	public Neo4jVertex next() {
		
		if (nodes.hasNext()) {
			return new Neo4jVertex(nodes.next(), graph);
		}
		else {
			throw new NoSuchElementException();
		}

	}

	@Override
	public void remove() {
		nodes.remove();
	}
}
