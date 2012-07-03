package experiments;

import experiments.queries.Query;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

import java.util.ArrayList;

import builders.RandomTreeBuilder;

public class Experiment {
	
	private RandomTreeBuilder builder;
	private ArrayList<Query> queries;

	public void addQuery(Query query) {

		// Adds the query and adds vertices for indexing.
		queries.add(query);
		builder.addVerticesToIndex(query.getQueryVertices());
	}
	
	public <V extends IVertex, E extends IEdge> void executeQueries(IPersistentGraph<V, E> graph) {

		// Iterates over and executes all queries on the graph.
		for (Query query : queries) {
			query.execute(graph);
		}
	}
}
