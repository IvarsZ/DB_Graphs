package converters;

import java.sql.Connection;
import java.sql.SQLException;

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
	
	/**
	 * 
	 * (Re)Initialises this object from reading a MySql database,
	 * where the object to read is represented by a specified name.
	 * 
	 * Of course this object and the specified object to read should be compatible.
	 * 
	 * @param connection - to the MySql database.
	 * @param name - name of the object to read in the MySql database.
	 * 
	 * @throws SQLException
	 */
	public void readFromMySql(Connection connection, String name) throws SQLException;
	
	/**
	 * 
	 * Initialises this object from reading a Neo4j database,
	 * where the object to read is represented by a specified name.
	 * 
	 * @param graphDb - the Neo4j database to read from.
	 * @param rootName - the name of the root representing the object to read.
	 * 
	 */
	public void readFromNeo4j(GraphDatabaseService graphDb, String rootName);
	
}
