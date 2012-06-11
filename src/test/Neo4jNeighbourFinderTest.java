package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import neighbourFinders.Neo4jNeighbourFinder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


public class Neo4jNeighbourFinderTest {

	private static final String DB_PATH = "neo4j-neighbour-finder-test-db";


	private static GraphDatabaseService graphDb;
	private static Neo4jNeighbourFinder neo4jNeighbourFinder;

	@BeforeClass
	public static void setup() {
		
		// Creates the graph database and the finder to test.
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		neo4jNeighbourFinder = new Neo4jNeighbourFinder();
		
		TestGraphs.writeGraph1ToNeo4j(graphDb);
	}
	
	// Real neighbour finder test methods.
	@Test
	public void noNeighbours() {
		
		// Node3 has no neighbours.
		assertTrue(neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 3)).isEmpty());
	}
	
	@Test
	public void oneNeighbour() {
		
		// Node7 has one neighbour Node5.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 7));
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 5)));
	}
	
	@Test
	public void onlyOutGoingNeighbours() {
		
		// Node6 has 2 neighbours (0 and 5) both are outgoing.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 6));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 0)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 5)));
	}
	
	@Test
	public void onlyIngoingNeighbours() {
		
		// Node8 has 2 neighbours (2 and 5) both are ingoing.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 8));
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 2)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 5)));
	}
	
	@Test
	public void multipleNeighbours() {
		
		// Node 5 has 6 neighbours 1, 2, 4, 6, 7, 8.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 5));
		assertEquals(6, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 1)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 2)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 4)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 6)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 7)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 8)));
	}
	
	// At depth tester
	@Test
	public void noNeighboursAtDepth2() {
		
		// Node3 has no neighbours at depth 2.
		assertTrue(neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 3), 2).isEmpty());
	}
	
	@Test
	public void oneNeighbourAtDepth3() {
		
		// Node7 has one neighbour at depth 3 - Node5.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 7), 3);
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 0)));
	}
	
	@Test
	public void multipleNeighboursAtDepth3() {
		
		// Node 0 has 3 neighbours at depth 3 - 2, 7, 8.
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(TestGraphs.getGraph1Node(graphDb, 0), 3);
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 2)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 7)));
		assertTrue(neighbours.contains(TestGraphs.getGraph1Node(graphDb, 8)));
	}
	
	@Test
	public void findNeigboursAtDepth3ForLargeGraph() {
		
		// Writes/creates the graph used in testing. 
		TestGraphs.writeGraph2ToNeo4j(graphDb);
		
		Node node3 = TestGraphs.getGraph2Node(graphDb, 3);
		
		Set<Node> neighbours = neo4jNeighbourFinder.findNeighbours(node3, 4);
		assertEquals(1, neighbours.size());

		assertTrue(neighbours.contains(TestGraphs.getGraph2Node(graphDb, 7)));
	}

	@AfterClass
	public static void closeTheDatabase()
	{
		graphDb.shutdown();
	}
}
