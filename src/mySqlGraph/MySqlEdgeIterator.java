package mySqlGraph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import exceptions.DataAccessException;

/**
 * 
 * Iterator over MySql edges, used to iterate over result sets containing edges.
 * 
 * @author iz2
 *
 */
public class MySqlEdgeIterator implements Iterator<MySqlEdge> {

	MySqlGraph graph;

	// TODO : resource closure.

	ResultSet edges;

	/**
	 * 
	 * @param edges - has to contain id field.
	 * @param graph
	 * 
	 */
	public MySqlEdgeIterator(ResultSet edges, MySqlGraph graph) {
		
		this.edges = edges;
		this.graph = graph;
	}

	@Override
	public boolean hasNext() {
		try {


			boolean hasNext = edges.next();
			if(hasNext) {

				// Reset the cursor back to its previous position
				edges.previous();
			}

			return hasNext;


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}			
	}

	@Override
	public MySqlEdge next() {
		try {


			// If there is a next edge creates and returns it.
			if (edges.next()) {
				return graph.getEdge(edges.getLong("id"));
			}

			// Otherwise throws NoSuchElementException.
			else {
				throw new NoSuchElementException("Edge Iterator " + edges + " has no more edges.");
			}


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public void remove() {

		// TODO : Implement?
		throw new UnsupportedOperationException("Removal not supported");
	}

}
