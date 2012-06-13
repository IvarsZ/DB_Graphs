package graph;

import graphInterfaces.IEdge;
import graphInterfaces.IVertex;

/**
 * 
 * Implementation of the IPropertyEdge.
 * 
 * @author iz2
 *
 */
public class Edge extends PropertyContainer implements IEdge {
	
	int id;
	IVertex start;
	IVertex end;

	@Override
	public IVertex getStart() {
		return start;
	}

	@Override
	public IVertex getEnd() {
		return end;
	}

	public Edge(int id, IVertex start, IVertex end) {
		super();
		this.start = start;
		this.end = end;
	}

	@Override
	public long getId() {
		return id;
	}
}
