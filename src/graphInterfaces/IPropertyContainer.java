package graphInterfaces;

import exceptions.DataAccessException;

/**
 * 
 * An interface for an object that can have properties.
 * 
 * A property is a key-value pair, where the key identifies the value of the property.
 * 
 * @author iz2
 *
 */
public interface IPropertyContainer<K, V> {
	
	/**
	 * Associates the specified value with the specified key
	 * If the key is already used to identify a value,
	 * the old value is replaced by the specified value.
	 * 
	 * @param key - the key identifying the value.
	 * @param value - the value associated with the key.
	 * 
	 * @throws DataAccessException 
	 */
	public void setProperty(K key, V value) throws DataAccessException;
	
	/**
	 * 
	 * Gets keys of all the properties.
	 * 
	 * @return - an iterator over all used keys.
	 */
	public Iterable<String> getKeys();
	
	/**
	 * 
	 * Returns the value associated with the specified key.
	 * 
	 * @param key - the key whose value is to be returned.
	 * 
	 * @return the value associated with the key, or null if there is no value associated with this key.
	 */
	public V getProperty(K key) throws DataAccessException;
}
