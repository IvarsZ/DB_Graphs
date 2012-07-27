package experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import util.Printer;
import experiments.builders.IBuilder;
import experiments.queries.QuerySet;
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

	private IBuilder builder;
	long executionTimes[][][];

	/**
	 * List of factories for creating graphs.
	 */
	private ArrayList<IFactory<?, ?>> factories;
	private String name;
	private ArrayList<QuerySet> queries;

	private ArrayList<Long> seeds;

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
		queries = new ArrayList<QuerySet>();
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
	 * Adds another set of queries to execute together on the built graphs.
	 * 
	 * @param query - the query to add.
	 * 
	 */
	public void addQuery(QuerySet query) {

		// Adds the query and adds vertices for indexing.
		queries.add(query);
		builder.addVerticesToIndex(query.getQueryVertices());
	}

	public void addVerticesToIndex(Set<Long> verticesToIndex) {
		builder.addVerticesToIndex(verticesToIndex);
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
				
				System.out.println("Executing experiment " + name + " for seed " + seed + " of " + factory.getPrintDetails());

				// creates a graph and builds it if necessary. // TODO : better mechanism (for forcing rewriting).
				IPersistentGraph<?, ?> graph = factory.createGraph(name + "_" + seed);
				if (forceRewrite || builder.isWrittenTo(graph, seed) == false) {
					System.out.println("Building the graph");
					graph.clear();
					builder.build(seed, graph);
				}

				// warms it up.
				System.out.println("Warmup");
				warmup(graph);

				// Then executes all queries on it, and saves the times.
				System.out.println("Executing queries");
				for (int k = 0; k < queries.size(); k++) {
					executionTimes[i][j][k] = queries.get(k).execute(graph);
					System.out.println(executionTimes[i][j][k]);
				}

				// Finally closes the graph.
				graph.close();
				System.out.println("Done");
			}
		}
	}

	private void warmup(IPersistentGraph<?, ?> graph) {

		// Executes queries from the experiment for at least 10 second.
		long start = System.currentTimeMillis();
		int i = 0;
		do {

			queries.get(i).execute(graph);
			
			i++;
			if (i >= queries.size()) {
				i = 0;
			}
		} while (System.currentTimeMillis() - start < 10000);
	}

	public void printExecutionTimes(Printer printer) {

		// Checks that experiment has been executed.
		if (executionTimes == null) {
			throw new IllegalStateException();
		}

		printer.println("Experiment " + name + " using " + builder.getPrintDetails());
		printer.println("");
		for (int i = 0; i < seeds.size(); i++) {
			printer.println("For seed " + seeds.get(i));

			for (int k = 0; k < queries.size(); k++) {
				printer.println("For queries " + queries.get(k).getPrintDetails());

				for (int j = 0; j < factories.size(); j++) {
					printer.println("\t\t" + factories.get(j).getPrintDetails() + " time is " + executionTimes[i][j][k]);
				}
			}
		}
	}

	public void exportExecutionTimesToR(File file) throws IOException {

		FileWriter fstream = new FileWriter(file, true);
		BufferedWriter out = new BufferedWriter(fstream);

		out.newLine();
		out.write("Experiment: " + name);
		out.newLine();
		out.write("Impl TSize TDepth TSeed QType QDepth QCount QTime");
		out.newLine();
		for (int i = 0; i < seeds.size(); i++) {
			for (int k = 0; k < queries.size(); k++) {
				for (int j = 0; j < factories.size(); j++) {
					out.write(factories.get(j).getPrintDetails() + " " + builder.getPrintDetails() + " " + seeds.get(i) + " " + queries.get(k).getPrintDetails() + " " + executionTimes[i][j][k]);
					out.newLine();
				}
			}
		}

		out.close();
	}

}
