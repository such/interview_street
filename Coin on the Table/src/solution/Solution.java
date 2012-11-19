package solution;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Solution {

	public static final int NO_PATH = -1;

	private Board board;

	static class Board {

		static enum Direction {
			UP('U', 0, -1), DOWN('D', 0, 1), LEFT('L', -1, 0), RIGHT('R', 1, 0);

			private final char letter;
			private final int yAdjustment;
			private final int xAdjustment;

			private Direction(char letter, int xAdjustment, int yAdjustment) {
				this.letter = letter;
				this.xAdjustment = xAdjustment;
				this.yAdjustment = yAdjustment;

			}

			public static Direction getValue(char letter) {
				for (Direction direction : Direction.values()) {
					if (direction.letter == letter) {
						return direction;
					}
				}
				return null;
			}

			public int getColumnAdjustment() {
				return xAdjustment;
			}

			public int getRowAdjustment() {
				return yAdjustment;
			}

			@Override
			public String toString() {
				return String.valueOf(letter);
			}
		}

		static abstract class CellType {

			abstract int getDefaultPathLength();

			abstract boolean canMoveTo(Direction direction);

			static CellType create(char letter) {
				if (letter == '*') {
					return new TargetCellType();
				} else {
					return new DirectionCellType(Direction.getValue(letter));
				}
			}
		}

		static class TargetCellType extends CellType {

			@Override
			int getDefaultPathLength() {
				return 0;
			}

			@Override
			boolean canMoveTo(Direction direction) {
				return false;
			}

			@Override
			public String toString() {
				return "*";
			}
		}

		static class DirectionCellType extends CellType {

			private final Direction direction;

			public DirectionCellType(Direction direction) {
				this.direction = direction;
			}

			@Override
			int getDefaultPathLength() {
				return NO_PATH;
			}

			@Override
			boolean canMoveTo(Direction direction) {
				return this.direction == direction;
			}

			@Override
			public String toString() {
				return direction.toString();
			}

		}

		class Cell implements Comparable<Cell> {

			int pathLength;
			int numberOfChanges = 0;
			CellType cellType;

			public Cell(CellType cellType) {
				this.cellType = cellType;
				this.pathLength = cellType.getDefaultPathLength();
			}

			@Override
			public int compareTo(Cell otherCell) {
				if (pathLength == NO_PATH) {
					return 1;
				} else {
					int changesDiff = numberOfChanges
							- otherCell.numberOfChanges;
					if (changesDiff == 0) {
						return pathLength - otherCell.pathLength;
					} else {
						return changesDiff;
					}
				}
			}

			@Override
			public String toString() {
				return cellType + " (" + pathLength + ", " + numberOfChanges
						+ ")";
			}
		}

		class AdjacentCell {
			Cell cell;
			Direction direction;

			public AdjacentCell(Cell cell, Direction direction) {
				this.cell = cell;
				this.direction = direction;
			}

		}

		private Cell[][] cells;
		private final int maxTurn;

		public Board(int rows, int columns, int maxTurn) {
			this.maxTurn = maxTurn;
			cells = new Cell[rows][columns];
		}

		public void addRow(int row, String cellsInRow) {
			for (int column = 0; column < cellsInRow.length(); column++) {
				cells[row][column] = new Cell(CellType.create(cellsInRow
						.charAt(column)));
			}
		}

		public void computeWeights() {
			boolean boardHasChanged = true;
			while (boardHasChanged) {
				display();
				boardHasChanged = false;
				for (int row = 0; row < cells.length; row++) {
					for (int column = 0; column < cells[row].length; column++) {
						Cell cell = cells[row][column];
						boardHasChanged |= computeCell(cell, row, column);
					}
				}
			}
		}

		private void display() {
			for (int row = 0; row < cells.length; row++) {
				for (int column = 0; column < cells[row].length; column++) {
					System.out.print(cells[row][column] + " ");
				}
				System.out.println("\n");
			}
			System.out.println("--------------------------");
		}

		public int getResult() {
			Cell firstCell = cells[0][0];
			if (firstCell.pathLength == NO_PATH) {
				return NO_PATH;
			} else {
				return firstCell.numberOfChanges;
			}
		}

		private boolean computeCell(Cell cell, int row, int column) {
			boolean hasChanged = false;

			List<Cell> adjacentCellsWeight = new ArrayList<Solution.Board.Cell>(
					4);
			for (AdjacentCell adjacentCell : getAdjacentCells(row, column)) {
				int adjacentCellPath = adjacentCell.cell.pathLength;
				if (adjacentCellPath != NO_PATH
						&& isNotTooFar(row, column, adjacentCell)) {

					Cell newCell = new Cell(cell.cellType);

					int changeAjustment = cell.cellType
							.canMoveTo(adjacentCell.direction) ? 0 : 1;

					newCell.pathLength = adjacentCellPath + 1;
					newCell.numberOfChanges = adjacentCell.cell.numberOfChanges
							+ changeAjustment;

					adjacentCellsWeight.add(newCell);
				}
			}

			Cell minCell = null;
			for (Cell adjacentCellWeight : adjacentCellsWeight) {
				if (minCell == null
						|| adjacentCellWeight.compareTo(minCell) < 0) {
					minCell = adjacentCellWeight;
				}

			}

			if (minCell != null && cell.compareTo(minCell) > 0) {
				cell.pathLength = minCell.pathLength;
				cell.numberOfChanges = minCell.numberOfChanges;
				hasChanged = true;
			}

			return hasChanged;
		}

		private boolean isNotTooFar(int row, int column,
				AdjacentCell adjacentCell) {
			int adjacentCellPath = adjacentCell.cell.pathLength;
			return adjacentCellPath <= maxTurn
					- (row + adjacentCell.direction.getRowAdjustment())
					- (column + adjacentCell.direction.getColumnAdjustment());
		}

		private List<AdjacentCell> getAdjacentCells(int row, int column) {
			List<AdjacentCell> result = new ArrayList<Solution.Board.AdjacentCell>(
					4);
			for (Direction direction : Direction.values()) {
				Cell cell = getAdjacentCell(direction, row, column);
				if (cell != null) {
					result.add(new AdjacentCell(cell, direction));
				}
			}

			return result;

		}

		private Cell getAdjacentCell(Direction direction, int row, int column) {
			int adjacentColumn = direction.getColumnAdjustment() + column;
			int adjacentRow = direction.getRowAdjustment() + row;
			if (adjacentColumn >= 0 && adjacentColumn < cells[row].length
					&& adjacentRow >= 0 && adjacentRow < cells.length) {
				return cells[adjacentRow][adjacentColumn];
			} else {
				return null;
			}
		}
	}

	public Solution(Board board) {
		this.board = board;
	}

	public static void main(String[] args) {
		InputStream input = System.in;

		Scanner scanner = new Scanner(input);
		int rows = scanner.nextInt();
		Board board = new Board(rows, scanner.nextInt(), scanner.nextInt());

		for (int row = 0; row < rows; row++) {
			board.addRow(row, scanner.next());
		}

		Solution s = new Solution(board);

		System.out.print(s.solve());
	}

	private int solve() {
		board.computeWeights();
		return board.getResult();
	}
}
