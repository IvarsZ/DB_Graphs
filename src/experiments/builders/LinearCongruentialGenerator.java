package experiments.builders;

/**
 * 
 * @author iz2
 *
 */
public class LinearCongruentialGenerator {
	
	long seed;
	long a;
	long c;
	
	protected LinearCongruentialGenerator(long seed) {
		this.seed = seed;
		
		// From MMIX.
		a = 6364136223846793005L; 
		c = 1442695040888963407L;
	}
	
	protected long next() {
		seed = a*seed + c;
		return seed;
	}
}
