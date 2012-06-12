package util;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import converters.Neo4jConverter.GraphRelationshipTypes;

public class Neo4jUtil {
	
	private static final String GRAPH_INDEX = "graphsIndex";
	private static final String ROOT_NAME_PROPERTY = "rootName";

	/**
	 * 
	 * Attempts to register a root node with the specified name in Neo4j for this graph.
	 * 
	 * @param graphDb - the neo4j database to which it is registered.
	 * @param rootName - the name with which the root is registered.
	 * 
	 * @return true if the root was successfully registered, false if there is already a root with the specified name.
	 */
	public static Node registerNeo4jRoot(GraphDatabaseService graphDb, String rootName) {

		// If a root with the rootName already exists, then doesn't create a new one and returns null.
		if (getNeo4jRoot(graphDb, rootName) != null) {
			return null;
		}

		// Otherwise creates a root with the rootName, and connects it to the reference node.
		Node root;
		Transaction tx = graphDb.beginTx();
		try {

			root = graphDb.createNode();

			root.setProperty(ROOT_NAME_PROPERTY, rootName);
			Index<Node> graphs = graphDb.index().forNodes(GRAPH_INDEX);
			graphs.add(root, ROOT_NAME_PROPERTY, rootName);

			Node referenceNode = graphDb.getReferenceNode();
			referenceNode.createRelationshipTo(root, GraphRelationshipTypes.REFERENCE_TO_GRAPH);

			tx.success();
		}
		finally {
			tx.finish();
		}

		return root;
	}
	
	/**
	 * 
	 * Gets the root node with the specified name from a neo4j database.
	 * 
	 * @param graphDb - the neo4j database.
	 * @param rootName - the name of the root to look for.
	 * 
	 * @return the root node, or null if it was not found.
	 */
	public static Node getNeo4jRoot(GraphDatabaseService graphDb, String rootName) {

		Index<Node> graphs = graphDb.index().forNodes(GRAPH_INDEX);
		return graphs.get(ROOT_NAME_PROPERTY, rootName).getSingle();
	}
}
