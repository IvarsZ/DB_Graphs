package mySqlGraph;

import graphInterfaces.IIndex;

public class MySqlVertexIndex implements IIndex<MySqlVertex> {
	
	private MySqlGraph graph;
	
	/**
	 * name of the table storing this index.
	 */
	private String tableName;

	@Override
	public void add(MySqlVertex entity, String key, String value) {
		// TODO Auto-generated method stub
		
		// TODO : complete duplicates.
		
	}

	@Override
	public Iterable<MySqlVertex> get(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
