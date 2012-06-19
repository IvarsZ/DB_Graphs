package neo4jGraph;

import graphInterfaces.IGraphOperator;
import graphInterfaces.IIndexManager;
import graphInterfaces.IPersistentGraph;

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

public class Neo4jGraph implements IPersistentGraph<Neo4jVertex, Neo4jEdge> {

	protected static enum RelTypes implements RelationshipType {
		UNDEFINED
	}

	private GraphDatabaseService graphDb;
	private String graphDbPath;
	private Transaction transaction;
	private Neo4jIndexManager index;

	/**
	 * 
	 * Creates a new Neo4j graph, by creating a new embedded database or opening existing one.
	 * 
	 * @param path - path to the the Neo4j database.
	 */
	public Neo4jGraph(String graphDbPath) {

		// Sets the path for the database and starts it.
		this.graphDbPath = graphDbPath;
		createDatabase();
		index = new Neo4jIndexManager();
	}

	private void createDatabase() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(graphDbPath);
		deleteReferenceNode();
	}

	@Override
	public Neo4jVertex createVertex() {
		ensureInTransaction();
		Node node = graphDb.createNode();
		return new Neo4jVertex(node, this);
	}

	@Override
	public Neo4jEdge createEdge(Neo4jVertex start, Neo4jVertex end)
			throws IllegalArgumentException {

		// If both start and end are from this graph,
		if (belongToGraph(start) && belongToGraph(end)) {

			// Creates a new relationship, and returns its edge.
			Node startNode = ((Neo4jVertex) start).getNode();
			Node endNode = ((Neo4jVertex) end).getNode();

			ensureInTransaction();
			Relationship edge = startNode.createRelationshipTo(endNode, RelTypes.UNDEFINED);
			return new Neo4jEdge(edge, this);
		}

		throw new IllegalArgumentException("Vertex " + start + " or " + end + " doesn't belong the the graph");
	}

	private boolean belongToGraph(Neo4jVertex vertex) {

		// If it is Neo4j vertex, 
		if (vertex instanceof Neo4jVertex) {

			// and belongs to the same graphDb, then the it is from this graph
			Node node = ((Neo4jVertex) vertex).getNode();
			return node.getGraphDatabase().equals(graphDb);
		}

		return false;
	}

	@Override
	public Neo4jVertex getVertex(long id) {

		try {
			return new Neo4jVertex(graphDb.getNodeById(id), this);
		}

		// If the vertex wasn't found return null, to conform to the interface.
		catch (NotFoundException e) {
			return null;
		}
	}

	@Override
	public Neo4jEdge getEdge(long id) {
		
		try {
			return new Neo4jEdge(graphDb.getRelationshipById(id), this);
		}

		// If the edge wasn't fNeo4jEdgeound return null, to conform to the interface.
		catch (NotFoundException e) {
			return null;
		}
	}

	@Override
	public Iterable<Neo4jVertex> getVertices() {

		return new Iterable<Neo4jVertex>() {

			@Override
			public Iterator<Neo4jVertex> iterator() {
				return new VertexIterator();
			}

		};
	}

	private class VertexIterator implements Iterator<Neo4jVertex> {

		Iterator<Node> nodeIterator;

		public VertexIterator() {
			nodeIterator = GlobalGraphOperations.at(graphDb).getAllNodes().iterator();
		}

		@Override
		public boolean hasNext() {
			return nodeIterator.hasNext();
		}

		@Override
		public Neo4jVertex next() {
			return new Neo4jVertex(nodeIterator.next(), Neo4jGraph.this);

		}

		@Override
		public void remove() {
			nodeIterator.remove();
		}

	}

	@Override
	public Iterable<Neo4jEdge> getEdges() {
		return new Iterable<Neo4jEdge>() {

			@Override
			public Iterator<Neo4jEdge> iterator() {
				return new EdgeIterator();
			}

		};
	}

	private class EdgeIterator implements Iterator<Neo4jEdge> {

		Iterator<Relationship> edgeIterator;

		public EdgeIterator() {
			edgeIterator = GlobalGraphOperations.at(graphDb).getAllRelationships().iterator();
		}

		@Override
		public boolean hasNext() {
			return edgeIterator.hasNext();
		}

		@Override
		public Neo4jEdge next() {
			return new Neo4jEdge(edgeIterator.next(), Neo4jGraph.this);
		}

		@Override
		public void remove() {
			edgeIterator.remove();
		}

	}
	
	@Override
	public IGraphOperator<Neo4jVertex, Neo4jEdge> getOperator() {
		return new Neo4jGraphOperator(this);
	}
	
	@Override
	public IIndexManager<Neo4jVertex, Neo4jEdge> index() {
		return index;
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
	private void deleteReferenceNode() {

		// Commits the deletion as soon as done.
		Transaction tx = graphDb.beginTx();
		try {
			graphDb.getReferenceNode().delete();
			tx.success();
		} catch (NotFoundException e) {

		} finally {
			tx.finish();
		}
	}

	@Override
	public void clear() {

		// Closes the database, deletes all its files.
		rollback();
		graphDb.shutdown();
		deleteFileOrDirectory(new File(graphDbPath));

		// Recreates the database.
		createDatabase();
	}

	@Override
	public void close() {
		rollback();
		graphDb.shutdown();
	}

	@Override
	public void commit() {

		if (transaction != null) {
			transaction.success();
			transaction.finish();
		}

		transaction = null;
	}

	@Override
	public void rollback() {

		if (transaction != null) {
			transaction.failure();
			transaction.finish();
		}

		transaction = null;
	}

	protected void ensureInTransaction() {
		if (transaction == null) {
			transaction = graphDb.beginTx();
		}
	}
}
