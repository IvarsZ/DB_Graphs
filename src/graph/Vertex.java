package graph;

import graphInterfaces.IVertex;

/**
 * 
 * Implementation of the IPropertyVertex.
 * 
 * @author iz2
 *
 */
public class Vertex extends PropertyContainer implements IVertex {
	
	private int id;
	
	public Vertex(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
}
