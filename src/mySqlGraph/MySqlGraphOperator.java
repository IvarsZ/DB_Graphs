package mySqlGraph;

import exceptions.DataAccessException;
import graphInterfaces.IGraphOperator;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IPersistentGraph.Direction;
import graphInterfaces.ITraverser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;





/**
 * 
 * MySql implementation of the graph operator.
 * 
 * @author iz2
 *
 */
class MySqlGraphOperator implements IGraphOperator<MySqlVertex, MySqlEdge> {

	private static final String MARK_DEPTH_COLUMN = "depth_in_findNeighbours";
	
	private MySqlTraverser traverser;

	private MySqlGraph graph;

	private PreparedStatement getOutgoingEdges;
	private PreparedStatement getIngoingEdges;

	private PreparedStatement markDepthStatement;
	private PreparedStatement getDepthStatement;
	
	// TODO : vertices from the same graph.

	protected MySqlGraphOperator(MySqlGraph mySqlGraph) throws SQLException {

			this.graph = (MySqlGraph) mySqlGraph;

			getOutgoingEdges = graph.getMySqlConnection().prepareStatement("SELECT end FROM " + graph.getEdgesTableName() + " WHERE start = ?");
			getIngoingEdges = graph.getMySqlConnection().prepareStatement("SELECT start FROM " + graph.getEdgesTableName() + " WHERE end = ?");

			markDepthStatement = graph.getMySqlConnection().prepareStatement("UPDATE " + graph.getNodesTableName() + " SET " + MARK_DEPTH_COLUMN + " = ? WHERE id = ? ;");
			getDepthStatement = graph.getMySqlConnection().prepareStatement("SELECT " + MARK_DEPTH_COLUMN + " FROM "  + graph.getNodesTableName() + " WHERE id = ? ;" );
	}
	
	@Override
	public ITraverser<MySqlVertex> createTraverser(int maxDepth, List<String> allowedEdgeTypes, Direction allowedDirection) {
		try {
			
			
			return new MySqlTraverser(graph, maxDepth, allowedEdgeTypes, allowedDirection);
			
			
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	@Override
	public Set<MySqlVertex> findCommonAncestors(MySqlVertex v1, MySqlVertex v2,
			int maxDepth, List<String> allowedEdgeTypes,
			IPersistentGraph.Direction allowedDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MySqlVertex> findLowestCommonAncestors(MySqlVertex v1,
			MySqlVertex v2, int maxDepth, List<String> allowedEdgeTypes,
			Direction allowedDirection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<MySqlVertex> findNeighbours(MySqlVertex start) {
		ResultSet outgoing = null;
		ResultSet ingoing = null;
		try {


			Set<MySqlVertex> neighbours = new HashSet<MySqlVertex>();

			// Queries all nodes that are connected to the node and adds their ids to the neighbours set.
			outgoing = executeGetOutgoingEdges(start.getId());
			while (outgoing.next()) {
				neighbours.add(new MySqlVertex(outgoing.getLong(1), graph));
			}

			// Queries all nodes that the node is connected to and adds their ids to the neighbours set.
			ingoing = executeGetIngoingEdges(start.getId());
			while (ingoing.next()) {
				neighbours.add(new MySqlVertex(ingoing.getLong(1), graph));
			}

			return neighbours;


		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			if (outgoing != null) try { outgoing.close(); } catch (SQLException e) { /* Ignore */ }
			if (ingoing != null) try { ingoing.close(); } catch (SQLException e) { /* Ignore */ }
		}
	}

	@Override
	public Set<MySqlVertex> findNeighbours(MySqlVertex start, int atDepth) {
		
		// TODO : proper resource closure.
		
		try {

		Set<MySqlVertex> neighbours = new HashSet<MySqlVertex>();

		createMarkDepthColumn();

		Queue<Long> q = new LinkedList<Long>();
		q.add(start.getId());
		executeMarkDepth(start.getId(), 1);

		while (q.isEmpty() == false) {

			long node = q.poll();

			// Gets the depth, and adds the node to neighbours if required (note nodeDepth is larger by one than actual depth).
			long nodeDepth = getDepth(node);
			if (nodeDepth - 1 == atDepth) {
				neighbours.add(new MySqlVertex(node, graph));
			}
			else if (nodeDepth > atDepth) {

				// Since traverses using BFS then all next traversed nodes will have at least the same depth.
				return neighbours;
			}

			// Gets all child nodes and adds them to the queue, if not visited.
			ResultSet outgoing = executeGetOutgoingEdges(node);
			try {
				visitChildNodes(q, outgoing, nodeDepth);
			} finally {
				outgoing.close();
			}
			ResultSet ingoing = executeGetIngoingEdges(node);
			try {
				visitChildNodes(q, ingoing, nodeDepth);
			} finally {
				ingoing.close();
			}
		}

		// Cleanup - delete markDepthColumn.
		deleteMarkDepthColumn();

		return neighbours;
		
		
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}

	private void createMarkDepthColumn() throws SQLException {
		Statement statement = graph.getMySqlConnection().createStatement();
		try {

			// Drops markDepthColumn if it already existed.
			try {
				statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " DROP " + MARK_DEPTH_COLUMN);

				// Dropped it.
				System.err.println("Dropped existing column " + MARK_DEPTH_COLUMN + " in findNeighbours ");
			}
			catch (SQLException e) {

				// Ignores, as the column shouldn't exist.
			} finally {
				
				// Adds the column.
				statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " ADD " + MARK_DEPTH_COLUMN + " BIGINT ;");	
			}


		} catch (SQLException e) {
			throw e;
		} finally {
			statement.close();
		}
	}

	private void deleteMarkDepthColumn() throws SQLException {
		Statement statement = graph.getMySqlConnection().createStatement();
		try {


			statement.executeUpdate("ALTER TABLE " + graph.getNodesTableName() + " DROP " + MARK_DEPTH_COLUMN);


		} catch (SQLException e) {
			throw e;
		} finally {
			statement.close();
		}
	}

	private void visitChildNodes(Queue<Long> q, ResultSet resultSet, long nodeDepth) throws SQLException {

		while (resultSet.next()) {

			// Gets the child id and its depth.
			long child = resultSet.getLong(1);
			long childDepth = getDepth(child);

			// If depth is 0 it hasn't been visited yet.
			if (childDepth == 0) {

				// Add to queue and mark depth (one larger than parrent depth).
				q.add(child);
				executeMarkDepth(child, nodeDepth + 1);
			}
		}
	}

	private void executeMarkDepth(long node, long depth) throws SQLException {
		markDepthStatement.setLong(1, depth);
		markDepthStatement.setLong(2, node);
		markDepthStatement.executeUpdate();
	}
	
	/**
	 * 
	 * @param node
	 * @param getStatement - the first retrieved column has to contain the depth.
	 * 
	 * @return
	 * 
	 * @throws SQLException
	 * 
	 */
	private long getDepth(long node) throws SQLException {

		// Executes the statement.
		getDepthStatement.setLong(1, node);
		ResultSet rs = getDepthStatement.executeQuery();
		
		try {

			
			// Only one row.
			rs.next();
			return rs.getLong(1);
			
			
		} finally {
			rs.close();
		}
	}

	private ResultSet executeGetOutgoingEdges(long startId) throws SQLException {
		getOutgoingEdges.setLong(1, startId);
		return getOutgoingEdges.executeQuery();
	}

	private ResultSet executeGetIngoingEdges(long endId) throws SQLException {
		getIngoingEdges.setLong(1, endId);
		return getIngoingEdges.executeQuery();
	}
}
