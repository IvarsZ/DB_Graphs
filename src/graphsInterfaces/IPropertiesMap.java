package graphsInterfaces;

import java.util.Map;

/**
 * 
 * An interface for an object that can have properties.
 * 
 * A property is a key-value pair, where the key identifies the value of the property.
 * 
 * @author iz2
 *
 */
public interface IPropertiesMap<K, V> {
	
	/**
	 * 
	 * Gets the properties map.
	 * 
	 * @return a map containing with the properties.
	 * 
	 */
	Map<K, V> getProperties();
}
