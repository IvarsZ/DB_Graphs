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
	
	private long id;
	
	public Vertex(long id) {
		this.id = id;
	}
	
	@Override
	public long getId() {
		return id;
	}
}
