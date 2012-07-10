package experiments;

import java.util.ArrayList;

import util.Printer;
import experiments.builders.RandomTree;
import experiments.queries.ACAQuery;
import graphFactories.MySqlFactory;
import graphFactories.Neo4jFactory;
import graphInterfaces.IPersistentGraph.Direction;

public class TestExperiment {

	public static void main(String args[]) {
		
		realExperiment1();
	}
	
	private static void realExperiment1() {
		
		
		
		// TODO : seeds cannot be negative (due to naming).
		
		// List of seeds used (100 random integers from random.org, within the maximum limit of +10^9.
				long seeds[] = {991779220, 982191841, 981924873, 958323592, 955414927, 940525787, 929003723, 918079373, 841557202, 818570214,
								807409789, 807245300, 770449769, 666051106, 664953277, 664096962, 632440094, 623859696, 558405828, 537119375,
								489775164, 472198901, 460553642, 436457814, 423204943, 410928190, 367888341, 327169419, 309648540, 230990089,
								229673776, 223110984, 200232437, 200008342, 173784571, 118779533, 73994277, 72672519, 31232871, 1690147,
								26105041, 35170844, 69130616, 79698772, 82081554, 97521146, 103426311, 144861804, 149334622, 163931098, 181515139,
								185381819, 252947598, 256021539, 261275564, 266869033, 291766013, 296769814, 302883319, 315041918, 345873469, 345885175,
								348230398, 371295104, 395274769, 440888284, 478768973, 502152502, 511551882, 512677781, 518530665, 555063679, 557319539,
								565748878, 574189959, 585657435, 590080882, 596320970, 597405002, 601925668, 605462559, 630862600, 634745316, 656908993,
								661178510, 682738517, 725402524, 726165253, 743502234, 770750403, 776044807, 799152684, 809772815, 810663366, 863036250,
								876020249, 876729484, 888656376, 889713313, 946546904};
		
		/*
		 * 
		 * Experiment will use 100 random trees of size 100 with depths 1, 11, 21, 31,..., 91.
		 * 
		 */
		int experimentCount = 10;
		int graphCount = 10;
		int size = 100;
		int queryCount = 100;
		
		for (int i = 0; i < experimentCount; i++) {
			
			int depth = i * 10 + 1;
			RandomTree randomTree = new RandomTree(size, depth);
			Experiment experiment = new Experiment("e1_rt" + (i + 1), randomTree);
			addAllFactories(experiment);
			
			// Adds seeds.
			for (int j = 0; j < graphCount; j++) {
				experiment.addSeed(seeds[i * 10 + j]);
			}
			
			// Adds queries.
			for (int k = 0; k < queryCount; k++) {
				experiment.addQuery(new ACAQuery(k % size, k * 2 % size, (k % depth) + 1, empty, Direction.BOTH));
			}
			
			experiment.executeQueries(false);
			experiment.printExecutionTimes(new Printer("realExperiment1"));
		}
	}
	
	/*
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
		IBuilder builder = new RandomTree(101, 10);
		Experiment experiment = new Experiment("urg_and_aca", builder);

		addAllFactories(experiment);
		experiment.addSeed(504952);
		experiment.addSeed(1259944358674910L);
		experiment.addQuery(new ACAQuery(5, 10, 1000, empty, Direction.INCOMING));
		experiment.addQuery(new ACAQuery(7, 15, 5500, empty, Direction.INCOMING));

		experiment.executeQueries(true);
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
	*/

	private static final ArrayList<String> empty = new ArrayList<String>();
	
	private static final void addAllFactories(Experiment experiment) {
		experiment.addFactory(new MySqlFactory());
		experiment.addFactory(new Neo4jFactory());
	}

}
