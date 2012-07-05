package experiments;

import java.util.ArrayList;

import experiments.builders.IBuilder;
import experiments.builders.RandomTree;
import experiments.builders.UniformRandomGraph;
import experiments.queries.CAQuery;
import experiments.queries.LCAQuery;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IPersistentGraph.Direction;

public class TestExperiment {

	public static void main(String args[]) {
		
		// TODO : add experiment reader and use files?

		//experiment1();
		//experiment2();
		experiment3();
	}

	private static void experiment1() {

		// Tests on random trees.
		IBuilder builder = new RandomTree(2500, 10);
		Experiment experiment = new Experiment("rtt1", builder);

		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(14124);
		experiment.addSeed(141242356236L);
		experiment.addQuery(new LCAQuery(500, 1000, 1000, empty, Direction.INCOMING));
		experiment.addQuery(new LCAQuery(750, 1500, 5500, empty, Direction.INCOMING));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}
	
	private static void experiment2() {
		
		// Tests on uniform random graphs.
		IBuilder builder = new UniformRandomGraph(25, 0.5);
		Experiment experiment = new Experiment("urg1", builder);

		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(504952);
		experiment.addSeed(1259944358674910L);
		experiment.addQuery(new LCAQuery(5, 10, 100, empty, Direction.INCOMING));
		experiment.addQuery(new LCAQuery(7, 15, 550, empty, Direction.INCOMING));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}
	
	private static void experiment3() {
		
		// Tests on random trees, and uses common ancestors queries.
		IBuilder builder = new RandomTree(2500, 10);
		Experiment experiment = new Experiment("rtt1", builder);

		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(14124);
		experiment.addSeed(141242356236L);
		experiment.addQuery(new CAQuery(500, 1000, 1000, empty, Direction.INCOMING));
		experiment.addQuery(new CAQuery(750, 1500, 5500, empty, Direction.INCOMING));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}

	private static final ArrayList<String> empty = new ArrayList<String>();

}
