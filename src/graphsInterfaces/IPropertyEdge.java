package graphsInterfaces;

/**
 * 
 * An interface for a property edge.
 * 
 * An edge connects two vertices of a graph. A vertex called start is connected to vertex called end.
 * Therefore each edge has a direction.
 * 
 * A property edge can have properties - key-value pairs. The key is a string, and the value is an object.
 * 
 * @author iz2
 *
 */
public interface IPropertyEdge extends IHasProperties<String, String> {
	
	/**
	 * 
	 * @return the start vertex of this edge.
	 * 
	 */
	public IPropertyVertex getStart();
	
	/**
	 * 
	 * @return the end vertex of this edge.
	 * 
	 */
	public IPropertyVertex getEnd();
}
