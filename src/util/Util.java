package util;

public class Util {
	
	/**
	 * 
	 * Performs the modulo operation, a mod m.
	 *
	 * @return a mod m, where the result is nonnegative.
	 */
	public static long mod(long a, long m) {
		
		long r = a % m;
		if (r < 0) {
			r += m;
		}
		
		return r;
	}

}
