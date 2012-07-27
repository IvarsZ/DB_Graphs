package experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import experiments.builders.RandomTree;
import experiments.queries.FNDQuery;
import experiments.queries.LCAQuery;
import experiments.queries.QuerySet;
import experiments.queries.RandomTreeQuerySet;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IPersistentGraph.Direction;

public class RandomTreeExperiment {

	private static enum QueryTypes {
		FND,
		LCA
	}

	private static final ArrayList<String> empty = new ArrayList<String>();

	/**
	 * 
	 * @param args - the first argument is the filename of the file containing the configuration of the experiment.
	 * 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		// Gets the file.
		File in = new File(args[0]);
		Scanner sc = new Scanner(in);

		// Second argument tells weather to rewrite.
		boolean doRewrite = false;
		if (args[1].equals("rw")) {
			doRewrite = true;
		}

		// The first line is the size of the random tree and the second is depth.
		long size = sc.nextLong();
		int treeDepth = sc.nextInt();

		// Sets up the experiment.
		RandomTree tree = new RandomTree(size, treeDepth);
		Experiment experiment = new Experiment("rt_" + size + "_" + treeDepth, tree);

		// Third argument tells which implementation to use.
		if (args[2].equals("neo4j")) {
			experiment.addFactory(new Neo4jFactory());
		}
		else if (args[2].equals("mysql")) {
			experiment.addFactory(new MySqlFactory("jdbc:mysql://localhost/RandomTreeExperiment", "iz2", ""));
		}
		else {
			System.out.println("no valid implementation specified (3rd argument), quiting");
			return;
		}

		// Gets and adds the seeds.
		for (int i = 3; i < args.length; i++) {
			experiment.addSeed(Long.parseLong(args[i ]));
		}

		int numberOfVerticesToIndex = sc.nextInt();
		Set<Long> verticesToIndex = new HashSet<Long>();
		for (int i = 0; i < numberOfVerticesToIndex; i++) {
			verticesToIndex.add(sc.nextLong());
		}
		experiment.addVerticesToIndex(verticesToIndex);

		// The next line is the number of query sets, and subsequent lines are the query sets.
		long querySetCount = sc.nextInt();
		for (int i = 0; i < querySetCount; i++) {


			// Reads the type and depth of the query set.
			String type = sc.next();
			int queryDepth = sc.nextInt();		
			QuerySet querySet = new RandomTreeQuerySet(type, queryDepth);

			// Calls the appropriate reading method for reading the queries.

			switch(QueryTypes.valueOf(type)) {
			case FND: {
				readFND(querySet, sc, queryDepth);
				break;
			}
			case LCA: {
				readLCA(querySet, sc, queryDepth);
				break;
			}
			}

			// Adds the query set.
			experiment.addQuery(querySet);
		}

		// Executes the queries and prints the execution times.
		experiment.executeQueries(doRewrite);
		experiment.exportExecutionTimesToR(in);
	}

	private static void readFND(QuerySet querySet, Scanner sc, int depth) {

		// First two ints are the depth and the query count.
		int queryCount = sc.nextInt();

		// Reads the vertex numbers for the queryCount queries and adds them to the set.
		for (int i = 0; i < queryCount; i++) {

			long vertexNumber = sc.nextLong();
			querySet.addQuery(new FNDQuery(vertexNumber, depth));
		}
	}

	private static void readLCA(QuerySet querySet, Scanner sc, int depth) {

		// First two ints are the depth and the query count.

		int queryCount = sc.nextInt();

		// Reads the vertex numbers for the queryCount queries and adds them to the set.
		for (int i = 0; i < queryCount; i++) {

			long v1Number = sc.nextLong();
			long v2Number = sc.nextLong();

			querySet.addQuery(new LCAQuery(v1Number, v2Number, depth, empty, Direction.INCOMING));
		}
	}

}
