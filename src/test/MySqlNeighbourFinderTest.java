package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import neighbourFinders.MySqlNeigbourFinder;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MySqlNeighbourFinderTest {

	private static final String tableName = "graph1";

	private static Connection con;
	
	private static MySqlNeigbourFinder mySqlNeigbourFinder;

	@BeforeClass
	public static void setup() {

		// Connects to the database.
		con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// TODO: visible password.
			con = DriverManager.getConnection("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
			
			// Writes/Creates the graph used in testing.
			TestGraphs.writeGraph1ToMySql(con, tableName);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		// Creates the finder.
		mySqlNeigbourFinder = new MySqlNeigbourFinder();
	}

	@Test
	public void noNeighbours() throws SQLException {

		// Node3 has no neighbours.
		assertTrue(mySqlNeigbourFinder.findNeighbours(con, tableName, 3).isEmpty());
	}

	@Test
	public void oneNeighbour() throws SQLException {

		// Node7 has one neighbour Node5.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 7);
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(5));
	}

	@Test
	public void onlyOutGoingNeighbours() throws SQLException {

		// Node6 has 2 neighbours (0 and 5) both are outgoing.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 6);
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(0));
		assertTrue(neighbours.contains(5));
	}

	@Test
	public void onlyIngoingNeighbours() throws SQLException {

		// Node8 has 2 neighbours (2 and 5) both are ingoing.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 8);
		assertEquals(2, neighbours.size());
		assertTrue(neighbours.contains(2));
		assertTrue(neighbours.contains(5));
	}

	@Test
	public void multipleNeighbours() throws SQLException {

		// Node 5 has 6 neighbours 1, 2, 4, 6, 7, 8.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 5);
		assertEquals(6, neighbours.size());
		assertTrue(neighbours.contains(1));
		assertTrue(neighbours.contains(2));
		assertTrue(neighbours.contains(4));
		assertTrue(neighbours.contains(6));
		assertTrue(neighbours.contains(7));
		assertTrue(neighbours.contains(8));
	}
	
	// At depth tester
	@Test
	public void noNeighboursAtDepth2() throws SQLException {
		
		// Node3 has no neighbours at depth 2.
		assertTrue(mySqlNeigbourFinder.findNeighbours(con, tableName, 3, 2).isEmpty());
	}
	
	@Test
	public void oneNeighbourAtDepth3() throws SQLException {
		
		// Node7 has one neighbour at depth 3 - Node5.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 7, 3);
		assertEquals(1, neighbours.size());
		assertTrue(neighbours.contains(0));
	}
	
	@Test
	public void multipleNeighboursAtDepth3() throws SQLException {
		
		// Node 0 has 3 neighbours at depth 3 - 2, 7, 8.
		Set<Integer> neighbours = mySqlNeigbourFinder.findNeighbours(con, tableName, 0, 3);
		assertEquals(3, neighbours.size());
		assertTrue(neighbours.contains(2));
		assertTrue(neighbours.contains(7));
		assertTrue(neighbours.contains(8));
	}

	@AfterClass
	public static void closeTheDatabase()
	{
		// Uncomment to delete data afterwards.
		// deleteTables;
	}
}
