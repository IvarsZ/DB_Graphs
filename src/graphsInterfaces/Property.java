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
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Property) {
			
			// Casts.
			@SuppressWarnings("rawtypes")
			Property p = (Property) obj;
			
			// Checks key and value equality.
			return key.equals(p.key) && value.equals((p.value));
		}
		
		// Not instance of property.
		return false;
	}
}
