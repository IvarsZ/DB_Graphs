package converters;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * 
 * Interface providing conversion methods for neo4j databases.
 * 
 * @author iz2
 *
 */
public interface Neo4jConvertable {
	
	/**
	 * Writes the object that implements this interface to a neo4j database.
	 * It is represented by a name, the name should be unique to the neo4j database,
	 * otherwise the object is not written.
	 * 
	 * @param graphDb - the neo4j database to which it is written.
	 * @param rootName - the name of the root node of this object in the database.
	 * @param indexer - used to decide if and how to index nodes in the database.
	 */
	public void writeToNeo4j(GraphDatabaseService graphDb, String rootName, INeo4jIndexer indexer);
	
}
