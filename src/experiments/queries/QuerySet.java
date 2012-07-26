package experiments.queries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import graphInterfaces.IEdge;
import graphInterfaces.IPersistentGraph;
import graphInterfaces.IVertex;

public class QuerySet {

	private ArrayList<IQueryTemplate> templates;

	public QuerySet() {

		templates = new ArrayList<IQueryTemplate>();
	}

	public void addQuery(IQueryTemplate template) {
		templates.add(template);
	}

	/**
	 * 
	 * Executes the set of queries on a give graph.
	 * 
	 * @param graph - the given graph.
	 * 
	 * @return execution time in ms.
	 * 
	 */
	public <V extends IVertex, E extends IEdge> long execute(IPersistentGraph<V, E> graph) {

		// Prepares queries.
		ArrayList<IPreparedQuery> queries = new ArrayList<IPreparedQuery>();
		for (IQueryTemplate template : templates) {
			queries.add(template.prepare(graph));
		}

		// Executes them and captures the time.
		long start, end;
		start = System.currentTimeMillis();
		for (IPreparedQuery query : queries) {
			query.execute();
		}
		end = System.currentTimeMillis();

		return end - start;
	}

	public Set<Long> getQueryVertices() {

		Set<Long> queryVertices = new HashSet<Long>();

		for (IQueryTemplate template : templates) {
			queryVertices.addAll(template.getQueryVertices());
		}

		return queryVertices;
	}

	public String getPrintDetails() {

		String printDetails = "";

		for (IQueryTemplate template : templates) {
			printDetails += "\t" + template.getPrintDetials() + "\n";
		}

		return printDetails;

	}
	
	protected int size() {
		return templates.size();
	}
}
