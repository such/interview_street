package solution;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

public class Solution {

	static class Child {
		int ratingScore;
		int numberOfCandies = -1;

		public Child(int ratingScore) {
			super();
			this.ratingScore = ratingScore;
		}

		@Override
		public String toString() {
			return "(" + ratingScore + ", " + numberOfCandies + ")";
		}

	}

	static class ChildInLine {
		public ChildInLine(Child child) {
			this.child = child;
		}

		Child child;
		ChildInLine previousChild;
		ChildInLine nextChild;

		int getRatingScore() {
			return child.ratingScore;
		}

		int getNumberOfCandies() {
			return child.numberOfCandies;
		}

		void setNumberOfCandies(int numberOfCandies) {
			child.numberOfCandies = numberOfCandies;
			computeNeighBoors();
		}

		void computeNeighBoors() {
			if (child.numberOfCandies > -1) {
				for (ChildInLine neighboor : new ChildInLine[] { previousChild,
						nextChild }) {
					if (neighboor != null) {
						if (neighboor.getRatingScore() > child.ratingScore) {
							if (neighboor.getNumberOfCandies() <= child.numberOfCandies) {
								neighboor
										.setNumberOfCandies(getNumberOfCandies() + 1);
							}
						} else if (neighboor.getNumberOfCandies() == -1) {
							neighboor.setNumberOfCandies(1);
						}
					}
				}
			}
		}

		@Override
		public String toString() {
			return previousChild.child + " <- " + child + " -> "
					+ nextChild.child;
		}
	}

	LinkedList<ChildInLine> childsInLine = new LinkedList<ChildInLine>();

	void addChild(Child child) {
		ChildInLine childInLine = new ChildInLine(child);
		if (!childsInLine.isEmpty()) {
			ChildInLine last = childsInLine.getLast();
			childInLine.previousChild = last;
			last.nextChild = childInLine;
		}
		childsInLine.add(childInLine);
	}

	int computeNumberOfCandies() {
		initializeChildsWithMinRate(findMinRate());
		int numberOfCandies = 0;
		for (ChildInLine child : childsInLine) {
			numberOfCandies += child.getNumberOfCandies();
		}
		return numberOfCandies;
	}

	private void initializeChildsWithMinRate(int minRate) {
		for (ChildInLine childInLine : childsInLine) {
			if (childInLine.getRatingScore() == minRate) {
				childInLine.setNumberOfCandies(1);
			}
		}
	}

	private Integer findMinRate() {
		int min = -1;
		for (ChildInLine childInLine : childsInLine) {
			if (min == -1) {
				min = childInLine.getRatingScore();
			} else {
				min = Math.min(min, childInLine.getRatingScore());
			}
		}
		return min;
	}

	public static void main(String[] args) {
		InputStream input = System.in;

		Scanner scanner = new Scanner(input);
		int childs = scanner.nextInt();

		Solution solution = new Solution();

		for (int child = 0; child < childs; child++) {
			int rating = scanner.nextInt();
			solution.addChild(new Child(rating));
		}

		System.out.print(solution.computeNumberOfCandies());
	}
}
