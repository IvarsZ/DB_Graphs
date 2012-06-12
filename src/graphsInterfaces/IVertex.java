package graphsInterfaces;

/**
 * 
 * An interface for a property vertex, which can have properties - key-value pairs.
 * The key is a string, and the value is a string.
 * 
 * @author iz2
 *
 */
public interface IVertex extends IPropertiesMap<String, String> {

	/**
	 * 
	 * Gets the id of the vertex in the graph its stored.
	 * 
	 * @return the id of the vertex.
	 * 
	 */
	public int getId();
}
