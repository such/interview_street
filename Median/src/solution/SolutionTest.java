package solution;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import solution.Solution.Operation;
import solution.Solution.OperationType;

public class SolutionTest {

	@Test(invocationCount = 100)
	public void test() {

		List<Operation> operations = new ArrayList<Operation>();
		for (int i = 0; i < 1000; i++) {

			OperationType type = Math.random() > 0.4 ? OperationType.ADD
					: OperationType.REMOVE;
			int sign = Math.random() > 0.8 ? -1 : 1;
			operations.add(new Operation(type,
					(int) (Math.random() * 100 * sign)));
		}

		Solution solution = new Solution(operations);
		solution.computeOperations();

		BruteForceSolution bfSolution = new BruteForceSolution(operations);
		bfSolution.compute();

		AssertJUnit.assertEquals(operations.toString(), solution.result,
				bfSolution.result);

	}

	@Test(invocationCount = 10)
	public void testPerf() {

		List<Operation> operations = new ArrayList<Operation>();
		for (int i = 0; i < 100000; i++) {
			OperationType type = Math.random() > 0.5 ? OperationType.ADD
					: OperationType.REMOVE;

			operations.add(new Operation(type,
					(int) (Math.random() * Integer.MAX_VALUE)));
		}

		Solution solution = new Solution(operations);
		solution.computeOperations();
		// solution.outputResult();

	}

	@Test
	public void testInsertEnd() {
		List<Operation> operations = new ArrayList<Operation>();
		for (int i = 0; i < 100000; i++) {
			operations.add(new Operation(OperationType.ADD, i));
		}

		Solution solution = new Solution(operations);
		solution.computeOperations();
	}

	@Test
	public void test3() {

		List<Operation> operations = new ArrayList<Operation>();
		for (int i = 0; i < 10000; i++) {

			OperationType type = Math.random() > 0.4 ? OperationType.ADD
					: OperationType.REMOVE;

			operations.add(new Operation(type, 1));
		}

		Solution solution = new Solution(operations);
		solution.computeOperations();

		BruteForceSolution bfSolution = new BruteForceSolution(operations);
		bfSolution.compute();

		AssertJUnit.assertEquals(solution.result, bfSolution.result);

	}

	@Test
	public void test2() {

		List<Operation> operations = new ArrayList<Operation>();

		operations.add(new Operation(OperationType.REMOVE, 1));
		operations.add(new Operation(OperationType.REMOVE, 1));
		operations.add(new Operation(OperationType.REMOVE, 1));
		operations.add(new Operation(OperationType.REMOVE, 1));
		operations.add(new Operation(OperationType.ADD, 1));
		operations.add(new Operation(OperationType.ADD, 2));

		Solution solution = new Solution(operations);
		solution.computeOperations();

		BruteForceSolution bfSolution = new BruteForceSolution(operations);
		bfSolution.compute();

		System.out.println(solution.result);
		AssertJUnit.assertEquals(solution.result, bfSolution.result);

	}
}
