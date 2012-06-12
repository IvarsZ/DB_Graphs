package test;

import static org.junit.Assert.assertEquals;
import graphs.Graph;
import graphsInterfaces.IGraph;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.Test;

import converters.MySqlConverter;

public class MySqlWriteReadTest {

	private static Connection con;
	
	@BeforeClass
	public static void setup() {

		// Connects to the database.
		con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");

			// TODO: visible password.
			con = DriverManager.getConnection("jdbc:mysql://iz2.host.cs.st-andrews.ac.uk:3306/iz2_db","iz2","2mH6=H-5");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void writeAndReadTheSame() throws SQLException {
		
		// Writes and reads the same graph, and tests if they are equal.
		MySqlConverter mySqlConverter = new MySqlConverter();
		IGraph graph1Write = TestGraphs.overwriteGraph1ToMySql(con);
		IGraph graph1Read = new Graph();
		mySqlConverter.readGraph(con, TestGraphs.graph1Name);
		
		assertEquals(graph1Write, graph1Read);
	}
}
