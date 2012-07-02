package builders;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.List;

public class PropertyIndexer implements IIndexer {
	
	
	private String indexName;
	private String keyToIndex;
	private List<String> valuesToIndex;

	public PropertyIndexer(String indexName, String keyToIndex, List<String> valuesToIndex) {

		this.indexName = indexName;
		this.keyToIndex = keyToIndex;
		this.valuesToIndex = valuesToIndex;
	}

	@Override
	public <V extends IVertex, E extends IEdge> void index(V vertex, IPersistentGraph<V, E> graph) {
		
		String value = vertex.getProperty(keyToIndex);
		if (valuesToIndex.contains(value)) {
			graph.index().forVertices(indexName).add(vertex, keyToIndex, value);
		}
		
		
	}
	
	

}
