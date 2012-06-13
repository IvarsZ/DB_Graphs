package neo4jGraph;

import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.tooling.GlobalGraphOperations;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

public class Neo4jGraph implements IGraph {
	
	protected static enum RelTypes implements RelationshipType {
		UNDEFINED
	}
	
	private GraphDatabaseService graphDb;
	
	// TODO : constructor.

	@Override
	public IVertex createVertex() throws IllegalArgumentException {
		
		return new Neo4jVertex(graphDb.createNode());
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException {
		
		// If both start and end are from this graph,
		if (belongToGraph(start) && belongToGraph(end)) {
			
			// Creates a new relationship, and returns its edge.
			Node startNode = ((Neo4jVertex) start).getNode();
			Node endNode = ((Neo4jVertex) end).getNode();
			Relationship edge = startNode.createRelationshipTo(endNode, RelTypes.UNDEFINED);
			return new Neo4jEdge(edge);
		}
		
		throw new IllegalArgumentException("Vertex " + start + " or " + end + " doesn't belong the the graph");
	}

	private boolean belongToGraph(IVertex vertex) {
		
		// If it is Neo4j vertex, 
		if (vertex instanceof Neo4jVertex) {
			
			// and belongs to the same graphDb, then the it is from this graph
			Node node = ((Neo4jVertex) vertex).getNode();
			return node.getGraphDatabase().equals(graphDb);
		}
		
		return false;
	}

	@Override
	public IVertex getVertex(int id) {
		return new Neo4jVertex(graphDb.getNodeById(id));
	}

	@Override
	public IEdge getEdge(int id) {
		return new Neo4jEdge(graphDb.getRelationshipById(id));
	}

	@Override
	public Iterator<IVertex> getVertices() {
		return new VertexIterator();
	}
	
	private class VertexIterator implements Iterator<IVertex> {
		
		Iterator<Node> nodeIterator;
		
		public VertexIterator() {
			nodeIterator = GlobalGraphOperations.at(graphDb).getAllNodes().iterator();
		}

		@Override
		public boolean hasNext() {
			return nodeIterator.hasNext();
		}

		@Override
		public IVertex next() {
			return new Neo4jVertex(nodeIterator.next());
			
		}

		@Override
		public void remove() {
			nodeIterator.remove();
		}
		
	}

	@Override
	public Iterator<IEdge> getEdges() {
		return new EdgeIterator();
	}
	
	private class EdgeIterator implements Iterator<IEdge> {
		
		Iterator<Relationship> edgeIterator;
		
		public EdgeIterator() {
			edgeIterator = GlobalGraphOperations.at(graphDb).getAllRelationships().iterator();
		}

		@Override
		public boolean hasNext() {
			return edgeIterator.hasNext();
		}

		@Override
		public IEdge next() {
			return new Neo4jEdge(edgeIterator.next());
		}

		@Override
		public void remove() {
			edgeIterator.remove();
		}
		
	}

	@Override
	public void clear() {
		
	}

}
