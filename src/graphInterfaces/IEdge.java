package graphInterfaces;


/**
 * 
 * An interface for a property edge.
 * 
 * An edge connects two vertices of a graph. A vertex called start is connected to vertex called end.
 * Therefore each edge has a direction. Besides each edge has a type, that allows to perform
 * some operations only on specified types of edges in the graph.
 * 
 * A property edge can have properties - key-value pairs. The key is a string, and the value is a string.
 * 
 * @author iz2
 *
 */
public interface IEdge extends IPropertyContainer<String, String> {
	
	/**
	 * 
	 * @return the start vertex of this edge.
	 * 
	 */
	public IVertex getStart();
	
	/**
	 * 
	 * @return the end vertex of this edge.
	 * 
	 */
	public IVertex getEnd();

	/**
	 * 
	 * Gets the id of the edge.
	 * 
	 * @return - the id of the edge.
	 * 
	 */
	public long getId();
	
	/**
	 * 
	 * Gets the type of the edge.
	 * 
	 * @return - the type of the edge.
	 * 
	 */
	public String getType();
}
