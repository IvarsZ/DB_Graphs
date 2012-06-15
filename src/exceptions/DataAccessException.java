package exceptions;

@SuppressWarnings("serial")
public class DataAccessException extends RuntimeException {

	
	public DataAccessException(Throwable cause) {
		initCause(cause);
	}
}
