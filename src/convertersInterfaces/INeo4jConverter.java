package convertersInterfaces;

import graphsInterfaces.IGraph;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 * 
 * Interface providing conversion methods for neo4j databases.
 * 
 * @author iz2
 *
 */
public interface INeo4jConverter {
	
	/**
	 * Writes the object that implements this interface to a neo4j database.
	 * It is represented by a name, the name should be unique to the neo4j database,
	 * otherwise the object is not written.
	 * 
	 * @param graphDb - the neo4j database to which it is written.
	 * @param graph - the graph that is written.
	 * @param rootName - the name of the root node of this object in the database.
	 * @param indexer - used to decide if and how to index nodes in the database.
	 */
	public void writeGraph(GraphDatabaseService graphDb, IGraph graph, String rootName, INeo4jIndexer indexer);
	
	/**
	 * 
	 * Reads a graph from a Neo4j database,
	 * where the graph to read is represented by the specified name.
	 * 
	 * @param graphDb - the Neo4j database to read from.
	 * @param rootName - the name of the root representing the object to read.
	 * @return TODO
	 * 
	 */
	public IGraph readGraph(GraphDatabaseService graphDb, String rootName);
	
}
