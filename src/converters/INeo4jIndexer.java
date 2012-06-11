package converters;

import org.neo4j.graphdb.Node;

/**
 * 
 * Interface for deciding how an object should be indexed when writing to neo4j.
 * 
 * @author iz2
 *
 */
public interface INeo4jIndexer {
	
	/**
	 * If required indexes a neo4j node.
	 * 
	 * @param graphDb
	 * @param node
	 */
	public void indexNode(Node node);

}
