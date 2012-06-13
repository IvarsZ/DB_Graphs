package mySqlGraphTest;

import mySqlGraph.MySqlGraph;
import graphInterfaces.IGraph;
import graphTests.GraphTest;

public class MySqlGraphTest extends GraphTest {

	@Override
	public IGraph createEmptyGraph() {
		return new MySqlGraph();
	}

}
