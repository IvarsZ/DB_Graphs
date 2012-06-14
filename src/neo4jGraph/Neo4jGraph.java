package neo4jGraph;

import java.io.File;
import java.util.Iterator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import graphInterfaces.IEdge;
import graphInterfaces.IGraph;
import graphInterfaces.IVertex;

public class Neo4jGraph implements IGraph {
	
	protected static enum RelTypes implements RelationshipType {
		UNDEFINED
	}
		
	private GraphDatabaseService graphDb;
	private String graphDbPath;
	
	/**
	 * 
	 * Creates a new Neo4j graph, by creating a new embedded database or opening existing one.
	 * 
	 * @param path - path to the the Neo4j database.
	 */
	public Neo4jGraph(String graphDbPath) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(graphDbPath);
		deleteReferenceNode();
		
		//System.out.println(graphDb.getReferenceNode().getId());
		this.graphDbPath = graphDbPath;
	}

	@Override
	public IVertex createVertex() {
		
		// TODO : what happens in neo4j error cases, and same for edges.
		
		Transaction tx = graphDb.beginTx();
		try {
			Node node = graphDb.createNode();
			tx.success();
			return new Neo4jVertex(node);
		} finally {
			tx.finish();
		}
	}

	@Override
	public IEdge createEdge(IVertex start, IVertex end)
			throws IllegalArgumentException {
		
		// If both start and end are from this graph,
		if (belongToGraph(start) && belongToGraph(end)) {
			
			// Creates a new relationship, and returns its edge.
			Node startNode = ((Neo4jVertex) start).getNode();
			Node endNode = ((Neo4jVertex) end).getNode();
			
			
			Transaction tx = graphDb.beginTx();
			try {
				Relationship edge = startNode.createRelationshipTo(endNode, RelTypes.UNDEFINED);
				tx.success();
				return new Neo4jEdge(edge);
			} finally {
				tx.finish();
			}
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
	public IVertex getVertex(long id) {
		return new Neo4jVertex(graphDb.getNodeById(id));
	}

	@Override
	public IEdge getEdge(long id) {
		return new Neo4jEdge(graphDb.getRelationshipById(id));
	}

	@Override
	public void close() {
		graphDb.shutdown();
	}
	
	@Override
	public Iterable<IVertex> getVertices() {
			
		return new Iterable<IVertex>() {

			@Override
			public Iterator<IVertex> iterator() {
				return new VertexIterator();
			}
			
		};
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
	public Iterable<IEdge> getEdges() {
		return new Iterable<IEdge>() {

			@Override
			public Iterator<IEdge> iterator() {
				return new EdgeIterator();
			}
			
		};
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
		
		// Closes the database, deletes all its files.
		graphDb.shutdown();
		deleteFileOrDirectory(new File(graphDbPath));
		
		// Recreates the database.
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(graphDbPath);
		deleteReferenceNode();
	}
	
	private static void deleteFileOrDirectory( final File file ) {
	    if ( file.exists() ) {
	        if ( file.isDirectory() ) {
	            for ( File child : file.listFiles() ) {
	                deleteFileOrDirectory( child );
	            }
	        }
	        file.delete();
	    }
	}
	
	/**
	 * Deletes the reference node (as not used).
	 */
	protected void deleteReferenceNode() {
		
		Transaction tx = graphDb.beginTx();
		try {
			graphDb.getReferenceNode().delete();
			tx.success();
		} catch (NotFoundException e) {
			
		} finally {
			tx.finish();
		}
	}
}
