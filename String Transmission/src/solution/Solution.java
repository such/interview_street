package solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Solution {

	private static final int MAX_STRING_SIZE = 1000;

	protected PrimeGenerator primeGenerator;

	protected String binaryString;

	protected int k;

	protected int n;

	public Solution(String binaryString, int k, PrimeGenerator primeGenerator) {
		this.binaryString = binaryString;
		this.n = binaryString.length();
		this.k = k;
		this.primeGenerator = primeGenerator;
	}

	/**
	 * The result is the total number of strings that can be generated with less
	 * than k errors (Sum(C(n,p), p=0 -> p=k) minus the total number of periodic
	 * strings that can be generated with less than k errors
	 */
	public BigInteger computeNumberOfPossibleStrings() {
		BigInteger totalNumberOfPossibleStrings = sumCnp(n, 0, k);
		BigInteger nbPeriodicStrings = numberOfPossiblePeriodicStrings();
		return totalNumberOfPossibleStrings.subtract(nbPeriodicStrings);
	}

	public BigInteger computeNumberOfPossibleStringsWithBruteForce() {
		BigInteger result = BigInteger.ZERO;
		for (String possibleString : generateAllPossibleStrings(binaryString, k)) {
			if (!isPeriodic(possibleString)) {
				result = result.add(BigInteger.ONE);
			}
		}
		return result;
	}

	private boolean isPeriodic(String possibleString) {
		Solution sol = new Solution(possibleString, 0, primeGenerator);
		for (Integer frequencePrime : primeGenerator.getPrimeFactors(n)) {
			PeriodicStringComputer periodicStringComputer = new PeriodicStringComputer(
					sol.splitBinaryString(frequencePrime));
			periodicStringComputer.computeMinimumDistancesFromPeriodicity();
			if (periodicStringComputer.minimumDistance == 0) {
				return true;
			}
		}
		return false;
	}

	private List<String> generateAllPossibleStrings(String string, int nbErrors) {
		if (string.length() == 0) {
			return Collections.singletonList("");
		} else if (nbErrors == 0) {
			return Collections.singletonList(string);
		} else {
			List<String> result = new LinkedList<String>();
			char firstChar = string.charAt(0);
			for (String subString : generateAllPossibleStrings(
					string.substring(1), nbErrors)) {
				result.add(firstChar + subString);
			}
			char newChar = firstChar == '0' ? '1' : '0';
			for (String subString : generateAllPossibleStrings(
					string.substring(1), nbErrors - 1)) {
				result.add(newChar + subString);
			}
			return result;
		}
	}

	public static BigInteger sumCnp(int n, int fromK, int toK) {
		if (n == toK && fromK == 0) {
			return BigInteger.valueOf(2).pow(n);
		} else {
			BigDecimal latestCnp = new BigDecimal(cnp(n, fromK));
			BigDecimal sum = latestCnp;
			for (int k = fromK + 1; k <= toK; k++) {
				latestCnp = latestCnp.multiply(BigDecimal.valueOf(n - k + 1))
						.divide(BigDecimal.valueOf(k));
				sum = sum.add(latestCnp);
			}
			return sum.toBigInteger();
		}
	}

	public static BigInteger sumCnpWithBruteForce(int n, int fromK, int toK) {
		BigInteger sum = BigInteger.ZERO;
		for (int i = fromK; i <= toK; i++) {
			sum = sum.add(cnp(n, i));
		}
		return sum;
	}

	public static BigInteger cnp(int n, int p) {
		BigInteger numerator = BigInteger.ONE;
		BigInteger denominator = BigInteger.ONE;
		for (int i = n - p + 1; i <= n; i++) {
			numerator = numerator.multiply(BigInteger.valueOf(i));
		}
		for (int j = 1; j <= p; j++) {
			denominator = denominator.multiply(BigInteger.valueOf(j));
		}
		return numerator.divide(denominator);
	}

	/**
	 * For each possible number of occurences of the periodic string (frequence)
	 * we compute how many periodic strings can be constructed with less than k
	 * errors. We only take into account prime frequences as for non prime
	 * frequences the periodic string have already been computed with its prime
	 * dividers
	 */
	private BigInteger numberOfPossiblePeriodicStrings() {

		List<Map<Integer, BigInteger>> numberOfKPeriodicStringList = new LinkedList<Map<Integer, BigInteger>>();

		Set<Integer> primeFactors = primeGenerator.getPrimeFactors(n);
		Map<Integer, BigInteger> numberOfKPeriodicString = numberOfKPeriodicString(primeFactors);
		numberOfKPeriodicStringList.add(numberOfKPeriodicString);

		int factorsNumber = 2;
		while (!numberOfKPeriodicString.keySet().isEmpty()) {
			// some strings might have been counted twice
			numberOfKPeriodicString = numberOfKPeriodicString(getMultiples(
					new TreeSet<Integer>(primeFactors), factorsNumber));
			numberOfKPeriodicStringList.add(numberOfKPeriodicString);
			factorsNumber++;
		}

		// remove periodic string that have been counted twice
		for (int i = numberOfKPeriodicStringList.size() - 1; i > 0; i--) {
			Map<Integer, BigInteger> multipleMap = numberOfKPeriodicStringList
					.get(i);
			for (Integer multiple : multipleMap.keySet()) {
				for (int j = i - 1; j >= 0; j--) {
					Map<Integer, BigInteger> factorMap = numberOfKPeriodicStringList
							.get(j);
					for (Integer factor : factorMap.keySet()) {
						if (multiple % factor == 0) {
							factorMap.put(factor, factorMap.get(factor)
									.subtract(multipleMap.get(multiple)));
						}
					}
				}
			}
		}

		BigInteger result = BigInteger.ZERO;
		for (Map<Integer, BigInteger> map : numberOfKPeriodicStringList) {
			for (BigInteger numberOfString : map.values()) {
				result = result.add(numberOfString);
			}
		}
		return result;
	}

	protected Set<Integer> getMultiples(SortedSet<Integer> factor, int size) {
		Set<Integer> result = new HashSet<Integer>();
		for (Set<Integer> factors : getAllFactorCombinationsOfSize(size, factor)) {
			Integer frequence = 1;
			for (Integer i : factors) {
				frequence = frequence * i;
			}
			if (frequence <= n) {
				result.add(frequence);
			}
		}
		return result;
	}

	protected Map<Integer, BigInteger> numberOfKPeriodicString(
			Set<Integer> frequences) {
		Map<Integer, BigInteger> result = new HashMap<Integer, BigInteger>(
				frequences.size());
		for (Integer frequencePrime : frequences) {
			BigInteger thisNumberOfPeriodicStrings = numberOfKPeriodicStrings(frequencePrime);
			if (thisNumberOfPeriodicStrings.compareTo(BigInteger.ZERO) > 0) {
				result.put(frequencePrime, thisNumberOfPeriodicStrings);
			}
		}
		return result;
	}

	protected BigInteger numberOfKPeriodicStrings(int frequence) {
		PeriodicStringComputer periodicStringComputer = new PeriodicStringComputer(
				splitBinaryString(frequence));
		periodicStringComputer.computeMinimumDistancesFromPeriodicity();
		if (periodicStringComputer.minimumDistance <= k) {
			// it is possible to build at least one periodic string
			// of frequence frequencePrime
			return BigInteger
					.valueOf(2)
					.pow(periodicStringComputer.numberOfClosestPeriodicStrings - 1)
					.multiply(
							computeNumberPossibleBytesChanges(
									k - periodicStringComputer.minimumDistance,
									periodicStringComputer.costsForChangingBytes));
		} else {
			return BigInteger.ZERO;
		}
	}

	protected static Set<Set<Integer>> getAllFactorCombinations(
			SortedSet<Integer> factors) {
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		for (int setSize = 2; setSize <= factors.size(); setSize++) {
			result.addAll(getAllFactorCombinationsOfSize(setSize, factors));
		}
		return result;
	}

	protected static Set<Set<Integer>> getAllFactorCombinationsOfSize(
			int setSize, SortedSet<Integer> factors) {
		Set<Set<Integer>> result = new HashSet<Set<Integer>>();
		if (setSize == 1) {
			for (Integer factor : factors) {
				result.add(Collections.singleton(factor));
			}
			return result;
		} else {
			for (Integer factor : factors.headSet(factors.last())) {
				for (Set<Integer> set : getAllFactorCombinationsOfSize(
						setSize - 1, factors.tailSet(factor + 1))) {
					Set<Integer> subResult = new HashSet<Integer>();
					subResult.add(factor);
					subResult.addAll(set);
					result.add(subResult);
				}
			}
			return result;
		}
	}

	/**
	 * Assumes n % numberOfParts == 0
	 */
	protected List<String> splitBinaryString(int numberOfParts) {
		List<String> split = new ArrayList<String>(numberOfParts);
		int subStringSize = n / numberOfParts;
		for (int part = 0; part < numberOfParts; part++) {
			split.add(binaryString.substring(part * subStringSize, (part + 1)
					* subStringSize));
		}
		return split;
	}

	private static class PeriodicStringComputer {

		int minimumDistance = 0;

		int numberOfClosestPeriodicStrings = 1;

		List<Integer> costsForChangingBytes;

		List<String> subStrings;

		public PeriodicStringComputer(List<String> subStrings) {
			this.subStrings = subStrings;
		}

		/**
		 * Compares all the strings and outputs a list of the minimum number of
		 * errors that must be made on each byte so that all strings are equal.
		 */
		public void computeMinimumDistancesFromPeriodicity() {
			int stringsLength = subStrings.get(0).length();
			costsForChangingBytes = new ArrayList<Integer>(stringsLength);
			for (int byteIndex = 0; byteIndex < stringsLength; byteIndex++) {
				int nb0 = 0;
				int nb1 = 0;
				for (String subString : subStrings) {
					if (subString.charAt(byteIndex) == '0') {
						nb0++;
					} else {
						nb1++;
					}
				}
				if (nb0 == nb1) {
					numberOfClosestPeriodicStrings++;
				} else {
					costsForChangingBytes.add(Math.max(nb0, nb1)
							- Math.min(nb0, nb1));
				}
				minimumDistance += Math.min(nb0, nb1);
			}
		}
	}

	/**
	 * Computes the number of combination of bytes that can be changed on the
	 * closest periodic string so that the number of errors is still less than
	 * k.
	 */
	protected BigInteger computeNumberPossibleBytesChanges(
			int numberOfPossibleChanges, List<Integer> minDistances) {
		SortedMap<Integer, Integer> distanceToNumberOfOccurences = new TreeMap<Integer, Integer>();
		for (Integer minDistance : minDistances) {

			Integer numberOfOccurences = distanceToNumberOfOccurences
					.get(minDistance);
			numberOfOccurences = numberOfOccurences == null ? 1
					: numberOfOccurences + 1;
			distanceToNumberOfOccurences.put(minDistance, numberOfOccurences);
		}

		return numberOfCombinationsLessThanDelta(distanceToNumberOfOccurences,
				numberOfPossibleChanges);
	}

	protected static BigInteger numberOfCombinationsLessThanDelta(
			SortedMap<Integer, Integer> distanceToNumberOfOccurences, int delta) {
		if (delta == 0 || distanceToNumberOfOccurences.isEmpty()) {
			return BigInteger.ONE; // empty Combination Allowed
		} else if (totalDistance(distanceToNumberOfOccurences) < delta) {
			int totalBytesLeft = totalBytesLeft(distanceToNumberOfOccurences);
			return sumCnp(totalBytesLeft, 0, totalBytesLeft);
		} else {
			BigInteger result = BigInteger.ZERO;
			Integer minDistance = distanceToNumberOfOccurences.firstKey();
			Integer numberOfOccurencesOfMinDistance = distanceToNumberOfOccurences
					.get(minDistance);

			for (int nbTimesDistanceTaken = 0; nbTimesDistanceTaken <= numberOfOccurencesOfMinDistance; nbTimesDistanceTaken++) {
				int newDelta = delta - nbTimesDistanceTaken * minDistance;
				if (newDelta >= 0) {
					SortedMap<Integer, Integer> newDistanceToNumberOfOccurences = new TreeMap<Integer, Integer>(
							distanceToNumberOfOccurences);
					newDistanceToNumberOfOccurences.remove(minDistance);
					result = result
							.add(cnp(numberOfOccurencesOfMinDistance,
									nbTimesDistanceTaken).multiply(
									numberOfCombinationsLessThanDelta(
											newDistanceToNumberOfOccurences,
											newDelta)));
				} else {
					break;
				}
			}
			return result;
		}
	}

	private static int totalBytesLeft(
			SortedMap<Integer, Integer> distanceToNumberOfOccurences) {
		int result = 0;
		for (Integer nbBytes : distanceToNumberOfOccurences.values()) {
			result += nbBytes;
		}
		return result;
	}

	private static int totalDistance(
			SortedMap<Integer, Integer> distanceToNumberOfOccurences) {
		int result = 0;
		for (Entry<Integer, Integer> entry : distanceToNumberOfOccurences
				.entrySet()) {
			result += entry.getKey() * entry.getValue();
		}
		return result;
	}

	static class PrimeGenerator {

		private final int n;

		protected SortedSet<Integer> primesBelowN;

		public PrimeGenerator(int n) {
			this.n = n;
			this.generatePrimesBelowN();
		}

		public Set<Integer> getPrimeFactors(final int number) {
			if (isPrime(number)) {
				return Collections.singleton(number);
			} else {
				Set<Integer> result = new HashSet<Integer>();
				for (Integer currentPrime : primesBelowN) {
					if (currentPrime <= Math.sqrt(number)) {
						if (number % currentPrime == 0) {
							result.add(currentPrime);
							result.addAll(getPrimeFactors(number / currentPrime));
							break;
						}
					} else {
						break;
					}
				}
				return result;
			}
		}

		public boolean isPrime(int k) {
			return primesBelowN.contains(k);
		}

		public Set<Integer> getPrimesBelow(int k) {
			if (k > 1) {
				return primesBelowN.subSet(2, k + 1);
			} else {
				return Collections.emptySet();
			}
		}

		private static class Element {
			boolean prime = true;

			final int i;

			public Element(int i) {
				this.i = i;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + i;
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj) {
					return true;
				}
				if (obj == null) {
					return false;
				}
				if (getClass() != obj.getClass()) {
					return false;
				}
				Element other = (Element) obj;
				if (i != other.i) {
					return false;
				}
				return true;
			}

		}

		/**
		 * Generate primes with erathostene sieve
		 */
		public void generatePrimesBelowN() {
			if (n > 1) {
				List<Element> all = new ArrayList<Element>(n + 1);

				for (int i = 0; i <= n; i++) {
					all.add(new Element(i));
				}
				all.get(0).prime = false;
				all.get(1).prime = false;

				int first = 2;
				double sqrtN = Math.sqrt(n);
				while (first < sqrtN) {
					for (int i = 2 * first; i <= n; i += first) {
						all.get(i).prime = false;
					}
					first = findFirstPrime(all, first);
				}

				List<Integer> primes = new LinkedList<Integer>();
				for (Element element : all) {
					if (element.prime) {
						primes.add(element.i);
					}
				}
				primesBelowN = new TreeSet<Integer>(primes);
			} else {
				primesBelowN = new TreeSet<Integer>();
			}
		}

		private int findFirstPrime(List<Element> all, int firstIndex) {
			int result = 0;
			int index = firstIndex + 1;
			while (result == 0 && index < all.size()) {
				Element element = all.get(index);
				if (element.prime) {
					result = element.i;
				}
				index++;
			}
			return result;
		}
	}

	public static List<Solution> readInput(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String s = br.readLine();
		int numberOfTests = Integer.valueOf(s);
		List<Solution> result = new ArrayList<Solution>(numberOfTests);
		int maxN = 0;
		br.mark(numberOfTests * (MAX_STRING_SIZE + 3));
		int testsRead = 0;
		while (testsRead < numberOfTests) {
			String[] nAndK = br.readLine().split(" ");
			int n = Integer.valueOf(nAndK[0]);
			br.readLine();
			maxN = Math.max(maxN, n);
			testsRead++;
		}
		PrimeGenerator primeGenerator = new PrimeGenerator(maxN);
		br.reset();
		while (result.size() < numberOfTests) {
			String[] nAndK = br.readLine().split(" ");
			int k = Integer.valueOf(nAndK[1]);
			String binaryString = br.readLine();
			result.add(new Solution(binaryString, k, primeGenerator));
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			List<Solution> solutions = readInput(System.in);
			for (Solution solution : solutions) {
				System.out.println(solution.computeNumberOfPossibleStrings()
						.mod(BigInteger.valueOf(1000000007)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}