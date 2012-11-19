package solution;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class Solution {

	static enum OperationType {
		REMOVE("r") {
			@Override
			public void process(Solution solution, Operation operation) {
				if (solution.contains(operation.number)) {
					solution.remove(operation.number);
				} else {
					throw new IllegalArgumentException();
				}

			}
		},
		ADD("a") {
			@Override
			public void process(Solution solution, Operation operation) {
				solution.add(operation.number);
			}
		};

		String letter;

		private OperationType(String letter) {
			this.letter = letter;
		}

		public abstract void process(Solution solution, Operation operation);

		public static OperationType valueWith(String letter) {
			for (OperationType op : OperationType.values()) {
				if (op.letter.equals(letter)) {
					return op;
				}
			}
			return null;
		}
	}

	static class Operation {
		OperationType operationType;
		int number;

		public Operation(OperationType operationType, int number) {
			super();
			this.operationType = operationType;
			this.number = number;
		}

		@Override
		public String toString() {
			return operationType.letter + " " + number;
		}
	}

	static class ListElement {
		int number;
		ListElement previous;
		ListElement next;

		public ListElement(int number, ListElement previous, ListElement next) {
			this.number = number;
			this.previous = previous;
			this.next = next;

			if (previous != null) {
				previous.next = this;
			}
			if (next != null) {
				next.previous = this;
			}
		}
	}

	List<Operation> operations;
	List<String> result;
	NavigableMap<Integer, LinkedList<ListElement>> allNumbers = new TreeMap<Integer, LinkedList<ListElement>>();
	ListElement currentMedian;
	ListElement firstListElement;
	ListElement lastListElement;
	boolean odd = false;

	public Solution(List<Operation> operations) {
		this.operations = operations;
		this.result = new ArrayList<String>(operations.size());
	}

	public boolean contains(int integer) {
		return allNumbers.containsKey(integer)
				&& allNumbers.get(integer).size() > 0;
	}

	private ListElement removeFromMap(int integer) {
		ListElement result = allNumbers.get(integer).getLast();
		ListElement previous = result.previous;

		if (previous != null) {
			while (previous.number == integer && previous.previous != null) {
				previous = previous.previous;
			}
			result = previous.next;
		}

		allNumbers.get(integer).remove(result);

		if (allNumbers.get(integer).size() == 0) {
			allNumbers.remove(integer);
		}

		if (result.previous != null) {
			result.previous.next = result.next;
		}
		if (result.next != null) {
			result.next.previous = result.previous;
		}
		return result;
	}

	private void addToMap(ListElement element) {
		LinkedList<ListElement> elements = allNumbers.get(element.number);
		if (elements == null) {
			elements = new LinkedList<ListElement>();
			allNumbers.put(element.number, elements);
		}
		elements.add(element);
	}

	protected void updateMedianAfterAddition(ListElement addedElement) {
		if (currentMedian == null) {
			currentMedian = lastListElement;
		} else if (addedElement.number > currentMedian.number && odd) {
			currentMedian = currentMedian.next;
		} else if (addedElement.number <= currentMedian.number && !odd) {
			currentMedian = currentMedian.previous;
		}
	}

	protected void updateMedianAfterRemoval(ListElement removedElement) {
		if (removedElement == currentMedian) {
			if (odd) {
				currentMedian = currentMedian.next;
			} else {
				currentMedian = currentMedian.previous;
			}
		} else if (removedElement.number > currentMedian.number && !odd) {
			currentMedian = currentMedian.previous;
		} else if (removedElement.number <= currentMedian.number && odd) {
			currentMedian = currentMedian.next;
		}
	}

	private void displayMedian() {
		if (currentMedian != null) {
			if (odd) {
				result.add(String.valueOf(currentMedian.number));
			} else {

				String median = BigDecimal.valueOf(currentMedian.number)
						.add(BigDecimal.valueOf(+currentMedian.next.number))
						.divide(BigDecimal.valueOf(2)).stripTrailingZeros()
						.toPlainString();

				result.add(median);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected void remove(int number) {
		odd = !odd;
		ListElement element = removeFromMap(number);
		if (element == firstListElement) {
			firstListElement = element.next;
		}
		if (element == lastListElement) {
			lastListElement = element.previous;
		}
		updateMedianAfterRemoval(element);
	}

	protected void add(int number) {
		odd = !odd;
		ListElement newElement;
		ListElement nextElement = getNextListElement(number);
		if (nextElement == null) {
			newElement = new ListElement(number, lastListElement, null);
			lastListElement = newElement;
		} else {
			newElement = new ListElement(number, nextElement.previous,
					nextElement);
		}
		addToMap(newElement);
		updateMedianAfterAddition(newElement);
	}

	private ListElement getNextListElement(int number) {
		LinkedList<ListElement> elementList = allNumbers.get(number);
		if (elementList == null) {
			Entry<Integer, LinkedList<ListElement>> higherEntry = allNumbers
					.higherEntry(number);
			if (higherEntry != null) {
				elementList = higherEntry.getValue();
			} else {
				return null;
			}
		}
		ListElement nextElement = elementList.getLast();
		ListElement previous = nextElement.previous;

		if (previous != null) {
			while (previous.number == nextElement.number
					&& previous.previous != null) {
				previous = previous.previous;
			}
			nextElement = previous.next;
		}
		return nextElement;
	}

	public void computeOperations() {
		for (Operation op : operations) {
			// System.out.println(op);
			try {
				process(op);
				displayMedian();
			} catch (IllegalArgumentException e) {
				result.add("Wrong!");
			}
			// System.out.println(this);
			System.out.println(result.get(result.size() - 1));
		}
	}

	private void process(Operation op) {
		op.operationType.process(this, op);
	}

	protected void outputResult() {
		for (String string : result) {
			System.out.println(string);
		}
	}

	@Override
	public String toString() {
		ListElement element = firstListElement;
		StringBuilder sb = new StringBuilder();
		while (element != null) {
			if (element == currentMedian) {
				sb.append("(");
			}
			sb.append(element.number);
			if (element == currentMedian) {
				sb.append(")");
			}
			sb.append(" ");
			element = element.next;
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		InputStream input = System.in;

		Scanner scanner = new Scanner(input);
		int numberOfOperations = scanner.nextInt();

		List<Operation> operations = new ArrayList<Operation>(
				numberOfOperations);

		while (operations.size() < numberOfOperations) {
			operations
					.add(new Operation(OperationType.valueWith(scanner.next()),
							scanner.nextInt()));
		}
		Solution solution = new Solution(operations);
		solution.computeOperations();
		solution.outputResult();
	}
}
