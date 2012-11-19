import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Find the no of positive integral solutions for the equations (1/x) + (1/y) = 1/N! (read 1 by n factorial) 
 * Print a single integer which is the no of positive integral solutions modulo 1000007.
 * @author Adrien
 *
 */
public class Solution {
	
	final Integer n;
	
	public Solution(Integer n) {
		if (n > 0){
			this.n = n;
		} else {
			throw new IllegalArgumentException("n should be stricly positive");
		}
	}
	
	private static class Element {
		boolean prime = true;
		final int i;
		
		public Element(int i){
			this.i = i;
		}
	}
	
	/**
	 * Generate primes with erathostene sieve
	 * @return
	 */
	public List<Integer> generatePrimesBelowN(){
		if (n > 1){
			List<Element> all = new ArrayList<Element>(n + 1);


			for (int i = 0 ; i <= n ; i++){
				all.add(new Element(i));
			}
			all.get(0).prime = false;
			all.get(1).prime = false;

			int first = 2;
			double sqrtN = Math.sqrt(n);
			while (first < sqrtN){
				for (int i = 2 * first ; i <= n ; i += first){
					all.get(i).prime = false;
				}
				first = findFirstPrime(all, first);
			}
			
			List<Integer> primes = new LinkedList<Integer>();
			for (Element element : all){
				if (element.prime){
					primes.add(element.i);
				}
			}
			return primes;
		} else {
			return Collections.emptyList();
		}
	}

	private int findFirstPrime(List<Element> all, int firstIndex) {
		int result = 0;
		int index = firstIndex + 1;
		while(result == 0 && index < all.size()){
			Element element = all.get(index);
			if (element.prime){
				result = element.i; 
			}
			index++;
		}
		return result;
	}

	/**
	 * The number of solutions is actually the number of dividers of (N!)^2
	 * (We have indeed:
	 * y = N! + (N!)^2 / x' where x' = x - N!, x' > 0)
	 * and we know x > N! (otherwise y < 0)
	 * 
	 * A prime number that divides (N!)^2 has to divide of the factor N(N-1)...2.
	 * It is then less than N
	 * We are looking for the decomposition in a product of prime numbers of (N!)^2  
	 * @return the number of dividers of (N!)^2
	 */
	public long numberOfSolutionsMod1000007() {
		List<Integer> primesBelowN = generatePrimesBelowN();
		List<Integer> exponentsMax = new ArrayList<Integer>();
		// for each prime below N look for its exponent in the decomposition in a product of prime numbers of N!
		for (Integer prime : primesBelowN){
			int exponentMax = 0;
			int quotient = n / prime;
			do {
				exponentMax += quotient;
				quotient = quotient / prime;
			} while (quotient != 0);
			// for N!^2 we must actually multiply by 2 the exponent we found (might be 0)
			exponentsMax.add(exponentMax * 2);
		}
		// each prime can be "taken" from 0 to the exponent we found to build a divider of N!^2. We add 1 to the exponent to take the 0 into account. 
		long result = 1;
		for (Integer exponentMax : exponentsMax){
			result = (result * (1 + exponentMax)) % 1000007;
		}
		return result;
	}
	
	public static void main(String[] args) {
		try {
			Integer n = readInput();
			Solution solution = new Solution(n);
			System.out.println(solution.numberOfSolutionsMod1000007());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static Integer readInput() throws IOException {
		BufferedReader br = 
			new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		return Integer.valueOf(s);
	}

}
