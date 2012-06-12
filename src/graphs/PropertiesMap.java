package graphs;

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
public class PropertiesMap {

	protected Map<String, String> properties;
	
	public PropertiesMap() {
		properties = new HashMap<String, String>();
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
}