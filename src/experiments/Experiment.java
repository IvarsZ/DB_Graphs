package experiments;

import java.util.ArrayList;

import experiments.builders.IBuilder;
import experiments.queries.Query;
import graphFactories.IFactory;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public class Experiment {

	private String name;
	private IBuilder builder;

	private ArrayList<IFactory<? extends IVertex, ? extends IEdge>> factories;
	private ArrayList<Long> seeds;
	private ArrayList<Query> queries;

	public Experiment(String name, IBuilder builder) {
		this.name = name;
		this.builder = builder;
		
		factories = new ArrayList<IFactory<? extends IVertex, ? extends IEdge>>();
		seeds = new ArrayList<Long>();
		queries = new ArrayList<Query>();
	}

	public <V extends IVertex, E extends IEdge> void addFactory(IFactory<V, E> factory) {
		factories.add(factory);
	}
	
	public void addSeed(long seed) {
		seeds.add(seed);
	}
	
	public void addQuery(Query query) {

		// Adds the query and adds vertices for indexing.
		queries.add(query);
		builder.addVerticesToIndex(query.getQueryVertices());
	}
	
	public void executeQueries() {

		// For every seed and
		for (Long seed : seeds) {

			// every factory
			for (IFactory<? extends IVertex, ? extends IEdge> factory : factories) {

				// creates an empty graph and builds it, then executes all queries on it.
				IPersistentGraph<? extends IVertex, ? extends IEdge> graph = factory.createEmptyGraph(name + "_" + seed);
				builder.build(seed, graph);
				executeQueries(graph);
				
				// Closes the graph.
				graph.close();
			}
		}
	}

	private <V extends IVertex, E extends IEdge> void executeQueries(IPersistentGraph<V, E> graph) {

		// Iterates over and executes all queries on the graph.
		for (Query query : queries) {
			query.execute(graph);
		}
	}
}
