package converters;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * Interface providing conversion methods for MySql databases.
 * 
 * @author iz2
 *
 */
public interface MySqlConvertable {
	
	/**
	 * 
	 * Writes this object to a MySql database.
	 * It is represented by a name, the name should be unique to the MySql database,
	 * otherwise the object is not written.
	 * 
	 * @param connection - a connection to the MySql database.
	 * @param name - the name of the object in the database.
	 * 
	 * @throws SQLException
	 */
	public void writeToMySql(Connection connection, String name) throws SQLException;
	
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
}
