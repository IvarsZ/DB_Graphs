package graphInterfaces;

/**
 * 
 * Interface for indexing vertices or edges.
 * 
 * @author iz2
 *
 * @param <T> - type that is indexed (vertices or edges).
 */
public interface IIndex<T extends IPropertyContainer<String, String>> {
	
	/**
	 * 
	 * Adds a key/value pair for a specified entity to the index.
	 * Adding the same key-value pair to the same entity yields no changes.
	 * 
	 * @param entity
	 * @param key
	 * @param value
	 * 
	 */
	public void add(T entity, String key, String value);
	
	/**
	 * 
	 * Returns exact matches from this index, given the key/value pair.
	 * Matches will be for key/value pairs just as they were added.
	 * 
	 * @param key
	 * @param value
	 * 
	 * @return
	 * 
	 */
	public Iterable<T> get(String key, String value);

}
