package graphs;

import graphsInterfaces.IEdge;
import graphsInterfaces.IVertex;

/**
 * 
 * Implementation of the IPropertyEdge.
 * 
 * @author iz2
 *
 */
public class PropertyEdge extends PropertiesMap implements IEdge {
	
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

	public PropertyEdge(IVertex start, IVertex end) {
		super();
		this.start = start;
		this.end = end;
	}
}
