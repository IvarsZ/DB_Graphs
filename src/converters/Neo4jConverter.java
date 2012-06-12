package converters;

import graphsInterfaces.IEdge;
import graphsInterfaces.IGraph;
import graphsInterfaces.IVertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

import util.Neo4jUtil;
import convertersInterfaces.INeo4jIndexer;
import convertersInterfaces.INeo4jConverter;

public class Neo4jConverter implements INeo4jConverter  {
	
	/**
	 * 
	 * Relationship types used when writing to ne4oj.
	 *
	 */
	public enum GraphRelationshipTypes implements RelationshipType {
		REFERENCE_TO_NODE,
		REFERENCE_TO_GRAPH,
		UNDEFINED
	}
	
	/**
	 * String representing the name of the relationship type property.
	 */
	private static final String RELATIONSHIP_TYPE_PROPERTY = "relationshipType";
	
	/**
	 * 
	 * @return the relationship type in Neo4j of the specified edge.
	 * 
	 */
	private static RelationshipType getRelationshipTypeOfEdge(IEdge iEdge) {
		
		// Gets the relationship type property.
		String relationshipType = iEdge.getProperties().get(RELATIONSHIP_TYPE_PROPERTY);
		
		// The relationship type is undefined.
		if (relationshipType == null) {
			return GraphRelationshipTypes.UNDEFINED;
		}
		
		return DynamicRelationshipType.withName(relationshipType);
	}
	
	@Override
	public void writeGraph(GraphDatabaseService graphDb, IGraph graph, String rootName, INeo4jIndexer indexer) {

		// Attempts to register a root node with the rootName.
		Node root = Neo4jUtil.registerNeo4jRoot(graphDb, rootName);
		if (root == null) {

			// Couldn't register the root, so doesn't write anything.
			return;
		}

		// For all vertices in the graph,
		Transaction tx;
		Map<Integer, Node> nodes = new HashMap<Integer, Node>(graph.getVertices().size());
		for (Entry<Integer, IVertex> vertex : graph.getVertices()) {
			
			tx = graphDb.beginTx();
			try {
				
				// creates a neo4j node with the properties of the vertex,
				Node node = graphDb.createNode();
				for (Entry<String, String> property : vertex.getValue().getProperties().entrySet()) {
					node.setProperty(property.getKey(), property.getValue());
				}
				
				// indexes the node.
				indexer.indexNode(node);
				
				// points the root to the node and saves the node.
				root.createRelationshipTo(node, GraphRelationshipTypes.REFERENCE_TO_NODE);
				nodes.put(vertex.getKey(), node);

				tx.success();
			}
			finally {
				tx.finish();
			}
		}

		// For all edges in the graph,
		for (Entry<Integer, IEdge> edge : graph.getEdges()) {
			
			// Gets the neo4j node of the start and the end vertex.
			Node start = nodes.get(edge.getValue().getStart().getId());
			Node end = nodes.get(edge.getValue().getEnd().getId());

			tx = graphDb.beginTx();
			try {
				
				// creates a neo4j relationship with the properties of the edge,
				Relationship relationship = start.createRelationshipTo(end, getRelationshipTypeOfEdge(edge.getValue()));
				for ( Entry<String, String> property : edge.getValue().getProperties().entrySet()) {
					relationship.setProperty(property.getKey(), property.getValue());
				}
				
				tx.success();
			}
			finally {
				tx.finish();
			}
		}

		/*
		 * TODO: Neo4j writer improvements
		 * 
		 * 	connect only unconnected parts, not every node.
		 * 
		 * 	write using BFS.
		 * 
		 * 	are references actually required, problem that the neighbour methods now have to know
		 * about the references and performance.
		 * 
		 */
	}
	
	@Override
	public IGraph readGraph(GraphDatabaseService graphDb, String rootName) {
		// TODO : Implement
		return null;
	}
}
