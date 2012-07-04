package experiments;

import java.util.ArrayList;

import util.Printer;

import experiments.builders.IBuilder;
import experiments.queries.Query;
import graphFactories.IFactory;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public class Experiment {

	private String name;
	private IBuilder builder;

	private ArrayList<IFactory<?, ?>> factories;
	private ArrayList<Long> seeds;
	private ArrayList<Query> queries;
	
	long executionTimes[][][];

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
		
		executionTimes = new long[seeds.size()][factories.size()][queries.size()];
		
		// For every seed and
		for (int i = 0; i < seeds.size(); i++) {
			long seed = seeds.get(i);
			
			// every factory
			for (int j = 0; j < factories.size(); j++) {
				IFactory<?, ?> factory = factories.get(j);
				
				// creates a graph and builds it if necessary.
				IPersistentGraph<?, ?> graph = factory.createGraph(name + "_" + seed);
				if (builder.isWrittenTo(graph, seed) == false) {
					graph.clear();
					builder.build(seed, graph);
				}
				
				// Then executes all queries on it, and saves the times.
				for (int k = 0; k < queries.size(); k++) {
					executionTimes[i][j][k] = queries.get(k).execute(graph);
				}
				
				// Finally closes the graph.
				graph.close();
			}
		}
	}
	
	public void printExecutionTimes() {
		
		// Checks that experiment has been executed.
		if (executionTimes == null) {
			throw new IllegalStateException();
		}
		
		Printer.println("Experiment " + name + " using " + builder.getPrintDetails());
		Printer.println("");
		for (int i = 0; i < seeds.size(); i++) {
			Printer.println("For seed " + seeds.get(i));
			
			for (int k = 0; k < queries.size(); k++) {
				Printer.println("\tFor query " + queries.get(k).getPrintDetials());
				
				for (int j = 0; j < factories.size(); j++) {
					Printer.println("\t\t" + factories.get(j).getPrintDetails() + " time is " + executionTimes[i][j][k]);
				}
				
			}
		}
		
	}
}
