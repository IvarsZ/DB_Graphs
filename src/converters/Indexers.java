package converters;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

import convertersInterfaces.INeo4jIndexer;

public class Indexers {
	
	/**
	 * @return an indexer that doesn't index any nodes.
	 */
	public static INeo4jIndexer indexNothing() {
		
		return new INeo4jIndexer() {

			@Override
			public void indexNode(Node node) {
				
			}
			
		};
	}
	
	/**
	 * Creates and returns an indexer that indexes the specified property in the specified index.
	 * 
	 * @param indexName - the name of the index.
	 * @param propertyName - the key of the property.
	 * 
	 * @return
	 */
	public static INeo4jIndexer indexNodeProperty(final String indexName, final String propertyName) {
		
		return new INeo4jIndexer() {

			@Override
			public void indexNode(Node node) {
				
				// If the node has the specified property,
				if (node.hasProperty(propertyName)) {
					
					// gets the index with indexName and adds the node and its property to the index.
					Index<Node> index = node.getGraphDatabase().index().forNodes(indexName);
					index.add(node, propertyName, node.getProperty(propertyName));
				}
			}
			
		};
	}

}
