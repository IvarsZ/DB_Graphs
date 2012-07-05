package experiments;

import java.util.ArrayList;

import org.hamcrest.Factory;

import experiments.builders.IBuilder;
import experiments.builders.RandomTree;
import experiments.builders.UniformRandomGraph;
import experiments.queries.ACAQuery;
import experiments.queries.FRNQuery;
import experiments.queries.LCAQuery;
import graphFactories.IFactory;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.IVertex;

public class TestExperiment {

	public static void main(String args[]) {

		// TODO : add experiment reader and use files?

		// experiment1();
		// experiment2();
		// experiment3();
		experiment4();
	}

	private static void experiment1() {

		// Tests finding lowest common ancestors on random trees.
		IBuilder builder = new RandomTree(2500, 10);
		Experiment experiment = new Experiment("rtt_and_lca", builder);

		addAllFactories(experiment);
		experiment.addSeed(14124);
		experiment.addSeed(141242356236L);
		experiment.addQuery(new LCAQuery(500, 1000, 1000, empty, Direction.INCOMING));
		experiment.addQuery(new LCAQuery(750, 1500, 5500, empty, Direction.INCOMING));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}

	private static void experiment2() {

		// Tests finding all common ancestors on uniform random graphs.
		IBuilder builder = new UniformRandomGraph(25, 0.5);
		Experiment experiment = new Experiment("urg_and_aca", builder);

		addAllFactories(experiment);
		experiment.addSeed(504952);
		experiment.addSeed(1259944358674910L);
		experiment.addQuery(new ACAQuery(5, 10, 100, empty, Direction.INCOMING));
		experiment.addQuery(new ACAQuery(7, 15, 550, empty, Direction.INCOMING));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}

	private static void experiment3() {

		// Tests finding neighbours (real) on random trees.
		IBuilder builder = new RandomTree(2500, 10);
		Experiment experiment = new Experiment("rtt_and_frn", builder);

		addAllFactories(experiment);
		experiment.addSeed(14124);
		experiment.addSeed(141242356236L);
		experiment.addQuery(new FRNQuery(500));
		experiment.addQuery(new FRNQuery(750));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}

	private static void experiment4() {

		// Tests finding neighbours (real) on random trees.
		IBuilder builder = new UniformRandomGraph(200, 0.3);
		Experiment experiment = new Experiment("urg_and_fnd", builder);

		addAllFactories(experiment);
		experiment.addSeed(504952);
		experiment.addSeed(1259944358674910L);
		experiment.addQuery(new FNDQuery(50, 15));
		experiment.addQuery(new FNDQuery(75, 24));

		experiment.executeQueries(false);
		experiment.printExecutionTimes();
	}

	private static final ArrayList<String> empty = new ArrayList<String>();
	
	private static final void addAllFactories(Experiment experiment) {
		experiment.addFactory(new Neo4jFactory());
		experiment.addFactory(new MySqlFactory());
	}

}
