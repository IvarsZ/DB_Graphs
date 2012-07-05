package experiments.builders;

/**
 * 
 * Generates stream of pseudo random numbers using recurence relation
 * 
 * x_(n+1) = (a*x_n + c) mod m.
 * 
 * @author iz2
 *
 */
class LinearCongruentialGenerator {
	
	private static final long LARGE_LONG = 100000000000000L;

	// Value used to generate the next random number and seed.
	long seed; 
	long a;
	long c;

	protected LinearCongruentialGenerator(long seed) {
		this.seed = seed;

		// From MMIX.
		// m is 2^64.
		a = 6364136223846793005L; 
		c = 1442695040888963407L;
	}

	/**
	 * 
	 * Generates next pseudo random long.
	 * 
	 * @return pseudo random long.
	 * 
	 */
	protected long nextLong() {
		seed = a*seed + c;
		return seed;
	}

	/**
	 * 
	 * Generates next pseudo random double in range [0,1] (uniformly).
	 * 
	 * @return the generated double.
	 * 
	 */
	protected double nextDouble() {
		
		// Converts the random long to a double in range [0,1].
		long r = nextLong() % LARGE_LONG;
		if (r < 0) {
			r += LARGE_LONG;
		}
		
		return r / ((double) LARGE_LONG );
	}
}
