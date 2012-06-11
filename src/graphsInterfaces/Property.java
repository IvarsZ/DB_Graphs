package graphsInterfaces;

/**
 * 
 * A class representing a property - key-value pair.
 * 
 * @author iz2
 *
 */
public class Property<K, V> {

	public K key;
	
	public V value;
	
	public Property(K key, V value) {
		this.key = key;
		this.value = value;
	}
}
