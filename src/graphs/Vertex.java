package graphs;

import graphsInterfaces.IVertex;

/**
 * 
 * Implementation of the IPropertyVertex.
 * 
 * @author iz2
 *
 */
public class Vertex extends PropertiesMap implements IVertex {
	
	private int id;
	
	public Vertex(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
}
