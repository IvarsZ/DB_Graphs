package util;

/**
 * 
 * Exception thrown if there was an error while interacting with the underlying data store of the graph.
 * 
 * @author iz2
 *
 */
@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

	
	public DataAccessException(Throwable cause) {
		initCause(cause);
	}
}
