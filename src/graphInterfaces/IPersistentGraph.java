package graphInterfaces;

public interface IPersistentGraph extends IGraph {
	
	/*
	 * 
	 * TODO : ??? add something like start transaction
	 * would be easier for Neo4j, don't have stick
	 * ensureInTransaction everywhere.
	 * 
	 * All of them would still be flat though. More work from the user part.
	 * The problem with ensureInTransaction and saving the graph in each vertex/edge
	 * is that now they aren't independent from the graph. Should they be? Probably no.
	 * So that's fine.
	 * 
	 */
	
	/**
	 * 
	 * Commits the last changes made to the graph.
	 * Makes all changes since the last commit persistent.
	 * 
	 * Other instances of the same graph, see the changes only
	 * when they are committed.
	 * 
	 */
	public void commit();
	
	/**
	 * 
	 * Rolls back the last changes made to the graph.
	 * Cancels all changes done since the last commit.
	 * 
	 */
	public void rollback();
	
	/**
	 * Properly closes the graph, releasing its resources.
	 */
	public void close();
}
