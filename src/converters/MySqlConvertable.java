package converters;

import java.sql.Connection;
import java.sql.SQLException;

public interface MySqlConvertable {
	
	/**
	 * 
	 * Writes the object that implements this interface to a MySql database.
	 * It is represented by a name, the name should be unique to the MySql database,
	 * otherwise the object is not written.
	 * 
	 * @param connection - a connection to the MySql database.
	 * @param name - the name of the object in the database.
	 * 
	 * @throws SQLException
	 */
	public void writeToMySql(Connection connection, String name) throws SQLException;
	
}
