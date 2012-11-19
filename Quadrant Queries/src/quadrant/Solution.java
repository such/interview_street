package quadrant;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

	public static class ReadingHead {
		boolean reflectX;
		boolean reflectY;

		public ReadingHead(boolean reflectX, boolean reflectY) {
			super();
			this.reflectX = reflectX;
			this.reflectY = reflectY;
		}

		public void process(TransformState state) {
			reflectX = reflectX ^ state.reflectReadingHeadX;
			reflectY = reflectY ^ state.reflectReadingHeadY;
		}

		public Quadrant read(Quadrant point) {
			return point.transform(reflectX, reflectY);
		}

	}

	public static enum ReflectionType {
		X, Y;
	}

	public static class TransformState {

		// static TransformState NO_TRANSFORM = new TransformState(false,
		// false);

		boolean reflectReadingHeadX;
		boolean reflectReadingHeadY;

		public TransformState(boolean reflectReadingHeadX,
				boolean reflectReadingHeadY) {
			super();
			this.reflectReadingHeadX = reflectReadingHeadX;
			this.reflectReadingHeadY = reflectReadingHeadY;
		}

		public void reflectX() {
			this.reflectReadingHeadX = !reflectReadingHeadX;
		}

		public void reflectY() {
			this.reflectReadingHeadY = !reflectReadingHeadY;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TransformState [reflectReadingHeadX=" + reflectReadingHeadX
					+ ", reflectReadingHeadY=" + reflectReadingHeadY + "]";
		}

	}

	public static abstract class Query {
		int firstPoint;
		int lastPoint;

		public Query(int firstPoint, int lastPoint) {
			super();
			this.firstPoint = firstPoint;
			this.lastPoint = lastPoint;
		}

		public abstract void process(Solution solution);
	}

	public static abstract class Reflection extends Query {
		public Reflection(int firstPoint, int lastPoint) {
			super(firstPoint, lastPoint);
		}
	}

	public static class ReflectionX extends Reflection {
		public ReflectionX(int firstPoint, int lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public void process(Solution solution) {
			solution.readingGrid.get(firstPoint).reflectX();
			if (lastPoint < solution.readingGrid.size() - 1) {
				solution.readingGrid.get(lastPoint + 1).reflectX();
			}
		}
	}

	public static class ReflectionY extends Reflection {
		public ReflectionY(int firstPoint, int lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public void process(Solution solution) {
			solution.readingGrid.get(firstPoint).reflectY();
			if (lastPoint < solution.readingGrid.size() - 1) {
				solution.readingGrid.get(lastPoint + 1).reflectY();
			}
		}
	}

	List<Map<Quadrant, Integer>> results = new ArrayList<Map<Quadrant, Integer>>();

	public static class Count extends Query {

		ReadingHead readingHead = new ReadingHead(false, false);

		public Count(int firstPoint, int lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public void process(Solution solution) {
			Map<Quadrant, Integer> result = new HashMap<Solution.Quadrant, Integer>(
					4);
			solution.results.add(result);
			result.put(Quadrant.NE, 0);
			result.put(Quadrant.SE, 0);
			result.put(Quadrant.NO, 0);
			result.put(Quadrant.SO, 0);
			int i = 0;
			for (TransformState state : solution.readingGrid) {
				readingHead.process(state);

				if (i >= firstPoint && i <= lastPoint) {
					Quadrant quadrant = readingHead
							.read(solution.points.get(i));
					result.put(quadrant, result.get(quadrant) + 1);
				}

				i++;
			}

		}
	}

	public static class Quadrant {
		static Quadrant NE = new Quadrant(true, true);
		static Quadrant NO = new Quadrant(false, true);
		static Quadrant SO = new Quadrant(false, false);
		static Quadrant SE = new Quadrant(true, false);

		boolean right;
		boolean top;

		public Quadrant(boolean right, boolean top) {
			this.right = right;
			this.top = top;
		}

		public Quadrant transform(boolean reflectX, boolean reflectY) {
			return new Quadrant(right ^ reflectY, top ^ reflectX);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (right ? 1231 : 1237);
			result = prime * result + (top ? 1231 : 1237);
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Quadrant other = (Quadrant) obj;
			if (right != other.right)
				return false;
			if (top != other.top)
				return false;
			return true;
		}

	};

	List<Quadrant> points;

	List<Query> queries;

	List<TransformState> readingGrid;

	public Solution(List<Quadrant> points, List<Query> queries) {
		this.points = points;
		this.queries = queries;
		readingGrid = new ArrayList<TransformState>(points.size());
		for (int i = 0; i < points.size(); i++) {
			readingGrid.add(new TransformState(false, false));
		}

	}

	public List<Map<Quadrant, Integer>> compute() {

		for (Query query : queries) {
			query.process(this);
			System.out.println(readingGrid);
		}

		return results;
	}

	private static Solution readInput() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		int numberOfPoints = Integer.valueOf(s);
		List<Quadrant> points = new ArrayList<Solution.Quadrant>(numberOfPoints);

		while (points.size() < numberOfPoints) {
			s = br.readLine();
			String[] coordinateString = s.split(" ");
			int x = Integer.valueOf(coordinateString[0]);
			int y = Integer.valueOf(coordinateString[1]);
			Quadrant point = new Quadrant(x > 0, y > 0);

			points.add(point);
		}

		s = br.readLine();
		int numberOfQueries = Integer.valueOf(s);
		List<Query> queries = new ArrayList<Solution.Query>(numberOfQueries);
		while (queries.size() < numberOfQueries) {
			String queryString = br.readLine();
			String[] queryArg = queryString.split(" ");
			int firstPoint = Integer.valueOf(queryArg[1]) - 1;
			int lastPoint = Integer.valueOf(queryArg[2]) - 1;
			Query query = null;
			if (queryArg[0].equals("C")) {
				query = new Count(firstPoint, lastPoint);
			} else if (queryArg[0].equals("X")) {
				query = new ReflectionX(firstPoint, lastPoint);
			} else if (queryArg[0].equals("Y")) {
				query = new ReflectionY(firstPoint, lastPoint);
			}
			queries.add(query);
		}
		return new Solution(points, queries);
	}

	public static void main(String[] args) {
		try {
			Solution solution = readInput();
			List<Map<Quadrant, Integer>> list = solution.compute();
			for (Map<Quadrant, Integer> result : list) {
				System.out.println(result.get(Quadrant.NE) + " "
						+ result.get(Quadrant.NO) + " "
						+ result.get(Quadrant.SO) + " "
						+ result.get(Quadrant.SE));
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}