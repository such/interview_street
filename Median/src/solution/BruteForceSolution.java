package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForceSolution {

	private List<Solution.Operation> operations;

	private List<Integer> numbers = new ArrayList<Integer>();

	List<String> result = new ArrayList<String>();

	public BruteForceSolution(List<Solution.Operation> operations) {
		this.operations = operations;
	}

	public void compute() {
		for (Solution.Operation operation : operations) {
			try {
				switch (operation.operationType) {
				case ADD:
					numbers.add(operation.number);
					break;
				case REMOVE:
					if (!numbers.remove(Integer.valueOf(operation.number))) {
						throw new IllegalArgumentException();
					}
					if (numbers.size() == 0) {
						throw new IllegalArgumentException();
					}
					break;
				}
				Collections.sort(numbers);
				String output;
				if (numbers.size() % 2 == 0) {
					output = Double.toString(
							(numbers.get(numbers.size() / 2 - 1) + numbers
									.get(numbers.size() / 2)) / 2.0)
							.replaceAll("\\.0$", "");
				} else {
					output = String.valueOf(numbers.get(numbers.size() / 2));
				}
				result.add(output);
			} catch (IllegalArgumentException e) {
				result.add("Wrong!");
			}
			// System.out.println(operation);
			// System.out.println(result.get(result.size() - 1));
			// System.out.println(numbers);
		}
	}
}
