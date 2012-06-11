package neighbourFinders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MySqlNeigbourFinder {

	/**
	 * Finds all neighbours of a given node. The neighbours are all nodes that have
	 * either ingoing or outgoing relationship with the node.
	 * 
	 * @param node - node whose neighbours are found. It 
	 * @throws SQLException 
	 */
	public Set<Integer> findNeighbours(Connection connection, String tableName, int node) throws SQLException {

		Set<Integer> neighbours = new HashSet<Integer>();


		Statement statement = connection.createStatement();

		// Queries all nodes that are connected to the node and adds their ids to the neighbours set.
		ResultSet resultSet = statement.executeQuery("SELECT start FROM " + tableName + "_edges WHERE end = " + node);
		while (resultSet.next()) {
			neighbours.add(resultSet.getInt(1));
		}

		// Queries all nodes that the node is connected to and adds their ids to the neighbours set.
		resultSet = statement.executeQuery("SELECT end FROM " + tableName + "_edges WHERE start = " + node);
		while (resultSet.next()) {
			neighbours.add(resultSet.getInt(1));
		}

		return neighbours;
	}

	/**
	 * Finds all neighbours of a given node. The neighbours are all nodes that have
	 * either ingoing or outgoing relationship with the node.
	 * 
	 * Only one instance can run at a time.
	 * 
	 * @param node - node whose neighbours are found. It 
	 * @throws SQLException 
	 */
	public Set<Integer> findNeighbours(Connection connection, String tableName, int startNode, int atDepth) throws SQLException {

		Set<Integer> neighbours = new HashSet<Integer>();

		String nodesTableName = tableName + "_nodes";
		String edgesTableName = tableName + "_edges";

		// TODO: hope there isn't a column with the same name, as it just drops the old one.
		String markDepthColumn = "depth_in_findNeighbours";

		Statement statement = connection.createStatement();

		// Drops markDepthColumn if it already existed.
		try {
			statement.executeUpdate("ALTER TABLE " + nodesTableName + " DROP " + markDepthColumn);
		}
		catch (SQLException e) { /* TODO : error message */}

		statement.executeUpdate("ALTER TABLE " + nodesTableName + " ADD " + markDepthColumn + " INT ;");
		PreparedStatement markDepthStatement = connection.prepareStatement("UPDATE " + nodesTableName + " SET " + markDepthColumn + " = ? WHERE id = ? ;");

		PreparedStatement getDepthStatement = connection.prepareStatement("SELECT " + markDepthColumn + " FROM "  + nodesTableName + " WHERE id = ? ;" );

		/* BFS
		create a queue Q
		3      enqueue v onto Q
		4      mark v
		5      while Q is not empty:
		6          t = Q.dequeue()
		7          if t is what we are looking for:
		8              return t
		9          for all edges e in G.incidentEdges(t) do
		10             o = G.opposite(t,e)
		11             if o is not marked:
		12                  mark o
		13                  enqueue o onto Q
		 */

		Queue<Integer> q = new LinkedList<Integer>();
		q.add(startNode);
		markDepth(startNode, markDepthStatement, 1);

		while (q.isEmpty() == false) {

			int node = q.poll();

			// Gets the depth, and adds the node to neighbours if required (note nodeDepth is larger by one than actual depth).
			int nodeDepth = getDepth(node, getDepthStatement);
			if (nodeDepth - 1 == atDepth) {
				neighbours.add(node);
			}
			else if (nodeDepth > atDepth) {

				// Since traverses using BFS then all next traversed nodes will have at least the same depth.
				return neighbours;
			}

			// Gets all child nodes and adds them to the queue, if not visited.
			ResultSet resultSet = statement.executeQuery("SELECT end FROM " + edgesTableName + " WHERE start = " + node);
			visitChildNodes(q, resultSet, markDepthStatement, getDepthStatement, nodeDepth);
			resultSet = statement.executeQuery("SELECT start FROM " + edgesTableName + " WHERE end = " + node);
			visitChildNodes(q, resultSet, markDepthStatement, getDepthStatement, nodeDepth);
		}

		// TODO: cleanup - delete markDepthColumn.

		return neighbours;
	}
	
	private static void visitChildNodes(Queue<Integer> q, ResultSet resultSet, PreparedStatement markDepthStatement, PreparedStatement getDepthStatement, int nodeDepth) throws SQLException {
		
		while (resultSet.next()) {

			// Gets the child id and its depth.
			int child = resultSet.getInt(1);
			int childDepth = getDepth(child, getDepthStatement);

			// If depth is 0 it hasn't been visited yet.
			if (childDepth == 0) {

				// Add to queue and mark depth (one larger than parrent depth).
				q.add(child);
				markDepth(child, markDepthStatement, nodeDepth + 1);
			}
		}
	}

	private static void markDepth(int node, PreparedStatement markStatement, int depth) throws SQLException {
		markStatement.setInt(1, depth);
		markStatement.setInt(2, node);
		markStatement.executeUpdate();
	}

	/**
	 * 
	 * @param node
	 * @param getStatement - the first retrieved column has to contain the depth.
	 * @return
	 * @throws SQLException
	 */
	private static int getDepth(int node, PreparedStatement getStatement) throws SQLException {
		getStatement.setInt(1, node);
		ResultSet rs = getStatement.executeQuery();
		
		// Only one row.
		if (rs.next()) {
			return rs.getInt(1);
		}
		
		// Shouldn't happen.
		return -1;
	}
}
