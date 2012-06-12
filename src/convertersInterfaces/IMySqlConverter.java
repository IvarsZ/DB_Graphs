package convertersInterfaces;

import graphsInterfaces.IGraph;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * Interface providing conversion methods for MySql databases.
 * 
 * @author iz2
 *
 */
public interface IMySqlConverter {
	
	/**
	 * 
	 * Writes a graph to a MySql database. It is represented by a name,
	 * the name should be unique to the MySql database,
	 * otherwise the graph is not written.
	 * 
	 * @param connection - a connection to the MySql database.
	 * @param graph - the graph that is written.
	 * @param name - the name of the object in the database.
	 * 
	 * @throws SQLException
	 * 
	 */
	public void writeGraph(Connection connection, IGraph graph, String name) throws SQLException;
	
	/**
	 * 
	 * Reads a graph from a MySql database,
	 * where the graph to read is represented by the specified name.
	 * 
	 * @param connection - to the MySql database.
	 * @param name - name of the graph to read in the MySql database.
	 * 
	 * @return the read graph.
	 * 
	 * @throws SQLException
	 * 
	 */
	public IGraph readGraph(Connection connection, String name) throws SQLException;
}
