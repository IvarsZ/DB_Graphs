package experiments;

import java.util.ArrayList;

import experiments.builders.IBuilder;
import experiments.builders.RandomTree;
import experiments.builders.UniformRandomGraph;
import experiments.queries.LCAQuery;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IPersistentGraph.Direction;

public class TestExperiment {

	public static void main(String args[]) {

		// Tests on random trees.
		/*IBuilder builder = new RandomTree(25000, 100);
		Experiment experiment = new Experiment("rtt1", builder);
		
		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(14124);
		experiment.addSeed(141242356236L);
		experiment.addQuery(new LCAQuery(5000, 10000, 10000, empty, Direction.INCOMING));
		experiment.addQuery(new LCAQuery(7500, 15000, 55000, empty, Direction.INCOMING));
		
		experiment.executeQueries();
		experiment.printExecutionTimes();*/
		
		// Tests on uniform random graphs.
		IBuilder builder = new UniformRandomGraph(2500, 0.5);
		Experiment experiment = new Experiment("urg1", builder);
		
		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(504952);
		experiment.addSeed(1259944358674910L);
		experiment.addQuery(new LCAQuery(500, 1000, 1000, empty, Direction.INCOMING));
		experiment.addQuery(new LCAQuery(750, 1500, 5500, empty, Direction.INCOMING));
		
		experiment.executeQueries();
		experiment.printExecutionTimes();
	}
	
	private static final ArrayList<String> empty = new ArrayList<String>();

}
