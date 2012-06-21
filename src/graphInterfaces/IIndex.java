package graphInterfaces;

/**
 * 
 * Interface for indexing vertices or edges.
 * An index allows to associate a vertex with a key-value pair, where
 * the key and value can be used to search for the vertex. Note
 * that multiple different vertices can be associated with the same
 * key-value pair.
 * 
 * The main purpose of indexing is to allow to search vertices/edges
 * by their properties if they are indexed.
 * 
 * @author iz2
 *
 * @param <T> - type that is indexed (vertices or edges).
 */
public interface IIndex<T extends IPropertyContainer<String, String>> {
	
	/**
	 * 
	 * Adds a key-value pair for a specified entity to the index.
	 * After addition the entity is associated with the specific pair.
	 * Adding the same key-value pair to the same entity yields no changes.
	 * 
	 * @param entity - entity which is associated with the key-value pair.
	 * @param key - the key of the key-value pair.
	 * @param value - the value of the key-value pair.
	 * 
	 */
	public void add(T entity, String key, String value);
	
	/**
	 * 
	 * Returns exact matches from this index, given the key-value pair.
	 * Matches will be for key-value pairs just as they were added.
	 * 
	 * @param key - the key that the searched entity must have.
	 * @param value - the value that the searched entity must have.
	 * 
	 * @return - entities with the specified key and value.
	 * 
	 */
	public Iterable<T> get(String key, String value);

}
