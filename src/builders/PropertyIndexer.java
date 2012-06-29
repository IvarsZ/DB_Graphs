package builders;

import java.util.ArrayList;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public class PropertyIndexer implements IIndexer {
	
	
	private String indexName;
	private String keyToIndex;
	private ArrayList<String> valuesToIndex;

	protected PropertyIndexer(String indexName, String keyToIndex, ArrayList<String> valuesToIndex, IPersistentGraph<IVertex, IEdge> graph) {

		this.indexName = indexName;
		this.keyToIndex = keyToIndex;
		this.valuesToIndex = valuesToIndex;
	}

	@Override
	public void index(IVertex vertex, IPersistentGraph<IVertex, IEdge> graph) {
		
		String value = vertex.getProperty(keyToIndex);
		if (valuesToIndex.contains(value)) {
			graph.index().forVertices(indexName).add(vertex, keyToIndex, value);
		}
		
		
	}
	
	

}
