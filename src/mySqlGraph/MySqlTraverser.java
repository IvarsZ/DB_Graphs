package mySqlGraph;

import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

import java.util.List;

class MySqlTraverser {
	
	public enum TraversalType {
		BFS,
		DFS
	}
	
	private MySqlGraph graph;
	private TraversalType traversalType;
	private int maxDepth;
	private List<String> allowedEdgeTypes;
	private Direction allowedDirection;
	
	public MySqlTraverser(MySqlGraph graph, TraversalType traversalType, int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) {
		
		
	}
	
	private Iterable<MySqlVertex> traverse(IVertex vertex) {
		
		
		
		return null;
	}

}
