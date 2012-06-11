package graphs;

import graphsInterfaces.IHasProperties;
import graphsInterfaces.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HasProperties implements IHasProperties<String, String> {

	protected Map<String, String> properties;

	public HasProperties() {
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
	public ArrayList<Property<String, String>> getProperties() {
		
		ArrayList<Property<String, String>> properties = new ArrayList<Property<String, String>>();
		
		for (Map.Entry<String, String> mapEntry : this.properties.entrySet()) {
			properties.add(new Property<String, String>(mapEntry.getKey(), mapEntry.getValue()));
		}
		
		return properties;
	}

}