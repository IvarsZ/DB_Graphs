package experiments;

import java.util.ArrayList;

import util.Printer;
import experiments.builders.IBuilder;
import experiments.queries.IQuery;
import graphFactories.IFactory;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

/**
 * 
 * Class for conducting experiments on graph.
 * 
 * It brings together graph generation and query execution on the graphs.
 * 
 * @author iz2
 *
 */
public class Experiment {
	
	private String name;
	private IBuilder builder;

	/**
	 * List of factories for creating graphs.
	 */
	private ArrayList<IFactory<?, ?>> factories;
	private ArrayList<Long> seeds;
	private ArrayList<IQuery> queries;
	
	long executionTimes[][][];

	/**
	 * 
	 * Creates a new experiment.
	 * 
	 * @param name - the name of the experiment.
	 * @param builder - the graph builder used to build graphs for this experiment.
	 * 
	 */
	public Experiment(String name, IBuilder builder) {
		this.name = name;
		this.builder = builder;
		
		factories = new ArrayList<IFactory<? extends IVertex, ? extends IEdge>>();
		seeds = new ArrayList<Long>();
		queries = new ArrayList<IQuery>();
	}

	/**
	 * 
	 * Adds a factory for creating graphs used in testing.
	 * Allows to add another graph implementation to the experiment.
	 * 
	 * @param factory - the factory to add.
	 * 
	 */
	public <V extends IVertex, E extends IEdge> void addFactory(IFactory<V, E> factory) {
		factories.add(factory);
	}
	
	/**
	 * 
	 * Adds another seed used for generating (building) graphs.
	 * 
	 * @param seed - the seed to add.
	 * 
	 */
	public void addSeed(long seed) {
		seeds.add(seed);
	}
	
	/**
	 * 
	 * Adds another query to execute on the built graphs.
	 * 
	 * @param query - the query to add.
	 * 
	 */
	public void addQuery(IQuery query) {

		// Adds the query and adds vertices for indexing.
		queries.add(query);
		builder.addVerticesToIndex(query.getQueryVertices());
	}
	
	/**
	 * 
	 * Executes all added queries on all built graphs from all seeds and factories.
	 * 
	 * @param forceRewrite - if true forces to actually rewrite all the graphs, instead
	 * of checking if the graph is already built.
	 * 
	 */
	public void executeQueries(boolean forceRewrite) {
		
		executionTimes = new long[seeds.size()][factories.size()][queries.size()];
		
		// For every seed and
		for (int i = 0; i < seeds.size(); i++) {
			long seed = seeds.get(i);
			
			// every factory
			for (int j = 0; j < factories.size(); j++) {
				IFactory<?, ?> factory = factories.get(j);
				
				// creates a graph and builds it if necessary. // TODO : better mechanism (for forcing rewriting).
				IPersistentGraph<?, ?> graph = factory.createGraph(name + "_" + seed);
				if (forceRewrite || builder.isWrittenTo(graph, seed) == false) {
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
