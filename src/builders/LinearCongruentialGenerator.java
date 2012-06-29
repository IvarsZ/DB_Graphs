package builders;

public class LinearCongruentialGenerator {
	
	long seed;
	long m;
	long a;
	long c;
	
	protected LinearCongruentialGenerator(long seed) {
		this.seed = seed;
		
		// From MMIX.
		m = 1L << 64;
		a = 6364136223846793005L; 
		c = 1442695040888963407L;
	}
	
	protected LinearCongruentialGenerator(long seed, long m, long a, long c) {
		this.seed = seed;
		this.m = m;
		this.a = a;
		this.c = c;
	}
	
	protected long next() {
		seed = (a*seed + c) % m;
		return seed;
	}
	
	

}
