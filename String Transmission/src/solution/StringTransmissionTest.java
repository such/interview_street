package solution;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class StringTransmissionTest {

	@Test
	public void testSplitString() {
		Solution solution = new Solution("010100", 1,
				new Solution.PrimeGenerator(5));
		AssertJUnit.assertEquals("010", solution.splitBinaryString(2).get(0));
		AssertJUnit.assertEquals("100", solution.splitBinaryString(2).get(1));

		AssertJUnit.assertEquals("01", solution.splitBinaryString(3).get(0));
		AssertJUnit.assertEquals("01", solution.splitBinaryString(3).get(1));

		AssertJUnit.assertEquals("0", solution.splitBinaryString(6).get(0));
		AssertJUnit.assertEquals("1", solution.splitBinaryString(6).get(1));
	}

	// @Test
	// public void numberOfCombinationsLessThanDeltaTest() {
	//
	// AssertJUnit.assertEquals(
	// 1,
	// Solution.numberOfCombinationsLessThanDelta(
	// Arrays.asList(new Integer[]{5, 6, 7}), 4));
	// AssertJUnit.assertEquals(
	// 2,
	// Solution.numberOfCombinationsLessThanDelta(
	// Arrays.asList(new Integer[]{5, 6, 7}), 5));
	// AssertJUnit.assertEquals(
	// 3,
	// Solution.numberOfCombinationsLessThanDelta(
	// Arrays.asList(new Integer[]{5, 6, 7}), 6));
	// AssertJUnit.assertEquals(
	// 5,
	// Solution.numberOfCombinationsLessThanDelta(
	// Arrays.asList(new Integer[]{5, 6, 7}), 11));
	// }

	@Test
	public void testGetPrimeFactors() {
		Solution.PrimeGenerator primeGenerator = new Solution.PrimeGenerator(
				1000);
		Set<Integer> expected = new HashSet<Integer>();
		expected.add(2);
		expected.add(7);
		AssertJUnit.assertEquals(expected, primeGenerator.getPrimeFactors(98));
	}

	@Test
	public void testReadInput() throws IOException {
		String data = "3\n" + "5 0\n" + "00000\n" + "3 1\n" + "001\n" + "3 3\n"
				+ "101";
		InputStream testInput = new ByteArrayInputStream(data.getBytes("UTF-8"));
		List<Solution> solution = Solution.readInput(testInput);
		AssertJUnit.assertEquals(0, solution.get(0).k);
		AssertJUnit.assertEquals("00000", solution.get(0).binaryString);
		AssertJUnit.assertEquals(3, solution.size());
	}

	@Test
	public void testSolution() throws IOException {
		String data = "3\n" + "5 0\n" + "00000\n" + "3 1\n" + "001\n" + "3 3\n"
				+ "101";
		InputStream testInput = new ByteArrayInputStream(data.getBytes("UTF-8"));
		List<Solution> solutions = Solution.readInput(testInput);

		AssertJUnit.assertEquals(BigInteger.ZERO, solutions.get(0)
				.computeNumberOfPossibleStrings());
		AssertJUnit.assertEquals(BigInteger.valueOf(3), solutions.get(1)
				.computeNumberOfPossibleStrings());
		AssertJUnit.assertEquals(BigInteger.valueOf(6), solutions.get(2)
				.computeNumberOfPossibleStrings());
	}

	@Test
	public void testSolutionWithBruteForce() throws IOException {
		String data = "3\n" + "5 0\n" + "00000\n" + "3 1\n" + "001\n" + "3 3\n"
				+ "101";
		InputStream testInput = new ByteArrayInputStream(data.getBytes("UTF-8"));
		List<Solution> solutions = Solution.readInput(testInput);

		AssertJUnit.assertEquals(BigInteger.ZERO, solutions.get(0)
				.computeNumberOfPossibleStringsWithBruteForce());
		AssertJUnit.assertEquals(BigInteger.valueOf(3), solutions.get(1)
				.computeNumberOfPossibleStringsWithBruteForce());
		AssertJUnit.assertEquals(BigInteger.valueOf(6), solutions.get(2)
				.computeNumberOfPossibleStringsWithBruteForce());
	}

	@Test
	public void getAllFactorsCombinationsTest() {
		NavigableSet<Integer> set = new TreeSet<Integer>();
		set.add(2);
		set.add(3);
		set.add(5);
		set.add(7);
		System.out.println(Solution.getAllFactorCombinations(set));
	}

	@Test(invocationCount = 100)
	public void testCompareSolutions() {

		String binaryString = "";
		int n = (int) (Math.random() * 20);
		for (int i = 0; i < n; i++) {
			char currentByte = Math.random() > 0.5 ? '0' : '1';
			binaryString = binaryString + currentByte;
		}
		int k = (int) (Math.random() * n);
		Solution solution = new Solution(binaryString, k,
				new Solution.PrimeGenerator(binaryString.length()));

		AssertJUnit.assertEquals("string=" + binaryString + ", k=" + k,
				solution.computeNumberOfPossibleStringsWithBruteForce(),
				solution.computeNumberOfPossibleStrings());
	}

	@Test
	public void testCompareSolutions2() {

		String binaryString = "000001";
		int k = 3;

		Solution solution = new Solution(binaryString, k,
				new Solution.PrimeGenerator(binaryString.length()));

		AssertJUnit.assertEquals("string=" + binaryString + ", k=" + k,
				solution.computeNumberOfPossibleStringsWithBruteForce(),
				solution.computeNumberOfPossibleStrings());
	}

	@Test
	public void testCompareSolutions30() {

		String binaryString = "000000000000000000000000000001";
		int k = 1;

		Solution solution = new Solution(binaryString, k,
				new Solution.PrimeGenerator(binaryString.length()));

		AssertJUnit.assertEquals("string=" + binaryString + ", k=" + k,
				BigInteger.valueOf(29),
				solution.computeNumberOfPossibleStrings());
	}

	@Test
	public void testCompareSolutions270() {

		String binaryString = "";
		for (int i = 0; i < 269; i++) {
			binaryString = binaryString + '0';
		}
		binaryString = binaryString + '1';
		int k = 1;

		Solution solution = new Solution(binaryString, k,
				new Solution.PrimeGenerator(binaryString.length()));

		AssertJUnit.assertEquals("string=" + binaryString + ", k=" + k,
				Solution.sumCnp(270, 0, 1).subtract(BigInteger.valueOf(2)),
				solution.computeNumberOfPossibleStrings());
	}

	@Test
	public void compareSumCnp() {
		AssertJUnit.assertEquals(Solution.sumCnpWithBruteForce(6, 0, 5),
				Solution.sumCnp(6, 0, 5));
	}

	@Test(invocationCount = 50, timeOut = 500)
	public void testSolution2() {
		String binaryString = "";
		for (int i = 0; i < 1000; i++) {
			char currentByte = Math.random() > 0.5 ? '0' : '1';
			binaryString = binaryString + currentByte;
		}
		int k = (int) (Math.random() * 1000);
		Solution solution = new Solution(binaryString, k,
				new Solution.PrimeGenerator(binaryString.length()));
		System.out.println(binaryString.length() + " " + k);
		System.out.println(solution.computeNumberOfPossibleStrings());
	}
}
