package experiments.queries;


public class RandomTreeQuerySet extends QuerySet {
	
	private String type;
	private int depth;
	
	public RandomTreeQuerySet(String type, int depth) {
		super();

		this.type = type;
		this.depth = depth;
	}
	
	@Override
	public String getPrintDetails() {
		
		return type + " " + depth + " " + size();
	}

}
