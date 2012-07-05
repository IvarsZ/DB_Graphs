package experiments.builders;

/**
 * 
 * @author iz2
 *
 */
public class LinearCongruentialGenerator {
	
	private static final long LARGE_LONG = 100000000000000L;

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

	protected double nextDouble() {
		
		// Converts the random long to a double in range [0,1].
		long r = next() % LARGE_LONG;
		if (r < 0) {
			r += LARGE_LONG;
		}
		
		return r / ((double) LARGE_LONG );
	}
}
