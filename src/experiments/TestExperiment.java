package experiments;

import java.util.ArrayList;

import experiments.builders.IBuilder;
import experiments.builders.RandomTreeBuilder;
import experiments.queries.LCAQuery;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IPersistentGraph.Direction;

public class TestExperiment {

	public static void main(String args[]) {

		IBuilder builder = new RandomTreeBuilder(1000, 100);
		Experiment experiment = new Experiment("rtt1", builder);
		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
		experiment.addSeed(14124);
		experiment.addQuery(new LCAQuery(100, 200, 200, empty, Direction.INCOMING));
		
		experiment.executeQueries();
	}
	
	private static final ArrayList<String> empty = new ArrayList<String>();

}
