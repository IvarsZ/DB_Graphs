package graph;


import graphInterfaces.IPropertyContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Implementation of the IHasProperties interface,
 * the key-value pairs of the properties are stored in a hash map.
 * 
 * @author iz2
 *
 */
public class PropertyContainer implements IPropertyContainer<String, String> {

	protected Map<String, String> properties;

	public PropertyContainer() {
		properties = new HashMap<String, String>();
	}

	@Override
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	@Override
	public String getProperty(String key) {
		return properties.get(key);
	}

	@Override
	public Iterable<String> getKeys() {
		return properties.keySet();
	}
}