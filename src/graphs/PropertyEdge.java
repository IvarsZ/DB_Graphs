package graphs;

import graphsInterfaces.IPropertyEdge;
import graphsInterfaces.IPropertyVertex;

/**
 * 
 * Implementation of the IPropertyEdge.
 * 
 * @author iz2
 *
 */
public class PropertyEdge extends HasProperties implements IPropertyEdge {
	
	IPropertyVertex start;
	IPropertyVertex end;

	@Override
	public IPropertyVertex getStart() {
		return start;
	}

	@Override
	public IPropertyVertex getEnd() {
		return end;
	}

	public PropertyEdge(IPropertyVertex start, IPropertyVertex end) {
		super();
		this.start = start;
		this.end = end;
	}
}
