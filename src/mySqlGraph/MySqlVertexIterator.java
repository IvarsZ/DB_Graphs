package mySqlGraph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import exceptions.DataAccessException;

public class MySqlVertexIterator implements Iterator<MySqlVertex> {

	MySqlGraph graph;

	// TODO : resource closure.

	ResultSet vertices;

	public MySqlVertexIterator(ResultSet vertices, MySqlGraph graph) {
		
		// TODO : what if the result set is improperly formated.
		this.vertices = vertices;
		this.graph = graph;
	}

	@Override
	public boolean hasNext() {
		try {


			boolean hasNext = vertices.next();
			if(hasNext) {

				// Reset the cursor back to its previous position.
				vertices.previous();
			}

			return hasNext;


		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	@Override
	public MySqlVertex next() {
		try {

			// If there is a next vertex creates and returns it.
			if (vertices.next()) {
				return new MySqlVertex(vertices.getLong("id"), graph);
			}

			// Otherwise throws NoSuchElementException.
			else {
				throw new NoSuchElementException("Vertex Iterator " + vertices + " has no more vertices.");
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

