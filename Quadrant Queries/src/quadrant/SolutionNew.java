package quadrant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SolutionNew {

	public static abstract class Query {
		final protected Point firstPoint;

		final protected Point lastPoint;

		public Query(Point firstPoint, Point lastPoint) {
			this.firstPoint = firstPoint;
			this.lastPoint = lastPoint;
		}

		public abstract void process(SolutionNew solution);
	}

	public static abstract class Reflection<T> extends Query {
		public Reflection(Point firstPoint, Point lastPoint) {
			super(firstPoint, lastPoint);
		}

		public abstract Quadrant transform(Quadrant quadrant);

		@Override
		public void process(SolutionNew solution) {
			Map<Quadrant, Set<Point>> pointsAfterReflection = new HashMap<Quadrant, Set<Point>>();
			for (Quadrant quadrant : Quadrant.quadrants) {
				Set<Point> modifiedPoints = new HashSet<Point>(solution.points
						.get(quadrant)
						.subSet(firstPoint, true, lastPoint, true));
				pointsAfterReflection.put(transform(quadrant), modifiedPoints);
				solution.points.get(quadrant).removeAll(modifiedPoints);
			}
			for (Quadrant quadrant : Quadrant.quadrants) {
				solution.points.get(quadrant).addAll(
						pointsAfterReflection.get(quadrant));
			}
		}

		protected boolean isIncluded(Reflection<T> reflection) {
			return reflection.firstPoint.compareTo(firstPoint) >= 0
					&& reflection.lastPoint.compareTo(o) <= 0;
		}

		protected boolean isExcluded(Reflection<T> reflection) {
			return reflection.lastPoint.compareTo(firstPoint) < 0
					|| reflection.firstPoint.compareTo(lastPoint) > 0;
		}

		public abstract List<T> merge(T reflection);
	}

	public static class ReflectionX extends Reflection<ReflectionX> {
		public ReflectionX(Point firstPoint, Point lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public Quadrant transform(Quadrant quadrant) {
			return quadrant.transformX();
		}

		@Override
		public String toString() {
			return "X" + firstPoint.indice + " " + lastPoint.indice;
		}

		@Override
		public List<ReflectionX> merge(ReflectionX reflection) {
			List<ReflectionX> result;
			if (isExcluded(reflection)) {
				result = new ArrayList<SolutionNew.ReflectionX>(2);
				result.add(this);
				result.add(reflection);
			} else if (isIncluded(reflection)) {

			}

		}
	}

	public static class ReflectionY extends Reflection<ReflectionY> {
		public ReflectionY(Point firstPoint, Point lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public Quadrant transform(Quadrant quadrant) {
			return quadrant.transformY();
		}

		@Override
		public String toString() {
			return "Y" + firstPoint.indice + " " + lastPoint.indice;
		}

		@Override
		public List<ReflectionY> merge(ReflectionY reflection) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class Count extends Query {

		public Count(Point firstPoint, Point lastPoint) {
			super(firstPoint, lastPoint);
		}

		@Override
		public void process(SolutionNew solution) {
			Map<Quadrant, Integer> result = new HashMap<Quadrant, Integer>(4);
			solution.results.add(result);

			for (Quadrant quadrant : Quadrant.quadrants) {
				int count = solution.points.get(quadrant)
						.subSet(firstPoint, true, lastPoint, true).size();
				result.put(quadrant, count);
			}
		}

		@Override
		public String toString() {
			return "C" + firstPoint.indice + " " + lastPoint.indice;
		}
	}

	public static class Quadrant {
		static Quadrant NE = new Quadrant(true, true);

		static Quadrant NO = new Quadrant(false, true);

		static Quadrant SO = new Quadrant(false, false);

		static Quadrant SE = new Quadrant(true, false);

		static List<Quadrant> quadrants = new ArrayList<Quadrant>(4);
		static {
			quadrants.add(NE);
			quadrants.add(NO);
			quadrants.add(SO);
			quadrants.add(SE);
		}

		final private boolean right;

		final private boolean top;

		public Quadrant(boolean right, boolean top) {
			this.right = right;
			this.top = top;
		}

		public Quadrant transformX() {
			return new Quadrant(right, top ^ true);
		}

		public Quadrant transformY() {
			return new Quadrant(right ^ true, top);
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
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Quadrant other = (Quadrant) obj;
			if (right != other.right) {
				return false;
			}
			if (top != other.top) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			if (this.equals(NE)) {
				return "NE";
			} else if (this.equals(NO)) {
				return "NO";
			} else if (this.equals(SE)) {
				return "SE";
			} else {
				return "SO";
			}
		}
	};

	public static class Point implements Comparable<Point> {

		static Map<Integer, Point> cache = new HashMap<Integer, Point>();

		final protected int indice;

		protected Point(int indice) {
			this.indice = indice;
		}

		public static Point valueOf(int indice) {
			Point point = cache.get(indice);
			if (point == null) {
				point = new Point(indice);
				cache.put(indice, point);
			}
			return point;
		}

		@Override
		public int compareTo(Point o) {
			return indice - o.indice;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + indice;
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
			Point other = (Point) obj;
			if (indice != other.indice) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "Point [indice=" + indice + "]";
		}
	}

	public static class PointWithCoordinates extends Point {
		final int x;
		final int y;

		public PointWithCoordinates(int indice, int x, int y) {
			super(indice);
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "PointWithCoordinates [x=" + x + ", y=" + y + ", indice="
					+ indice + "]";
		}

	}

	Map<Quadrant, TreeSet<Point>> points = new HashMap<Quadrant, TreeSet<Point>>();

	List<Query> queries;

	List<Map<Quadrant, Integer>> results = new ArrayList<Map<Quadrant, Integer>>();

	public SolutionNew(List<PointWithCoordinates> points, List<Query> queries) {
		this.points.put(Quadrant.NE, new TreeSet<Point>());
		this.points.put(Quadrant.NO, new TreeSet<Point>());
		this.points.put(Quadrant.SE, new TreeSet<Point>());
		this.points.put(Quadrant.SO, new TreeSet<Point>());

		for (PointWithCoordinates point : points) {
			Quadrant quadrant = new Quadrant(point.x > 0, point.y > 0);

			this.points.get(quadrant).add(point);

		}
		this.queries = queries;

	}

	public List<Map<Quadrant, Integer>> compute() {

		for (Query query : queries) {
			query.process(this);
		}

		return results;
	}

	private static SolutionNew readInput() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		int numberOfPoints = Integer.valueOf(s);

		List<PointWithCoordinates> points = new ArrayList<PointWithCoordinates>(
				numberOfPoints);
		int indice = 1;
		while (points.size() <= numberOfPoints) {
			s = br.readLine();
			String[] coordinateString = s.split(" ");
			int x = Integer.valueOf(coordinateString[0]);
			int y = Integer.valueOf(coordinateString[1]);

			points.add(new PointWithCoordinates(indice, x, y));
			indice++;
		}

		s = br.readLine();
		int numberOfQueries = Integer.valueOf(s);
		List<Query> queries = new ArrayList<Query>(numberOfQueries);
		while (queries.size() < numberOfQueries) {
			String queryString = br.readLine();
			String[] queryArg = queryString.split(" ");
			Point firstPoint = Point.valueOf(Integer.valueOf(queryArg[1]));
			Point lastPoint = Point.valueOf(Integer.valueOf(queryArg[2]));
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
		return new SolutionNew(points, queries);
	}

	public static void main(String[] args) {
		try {
			SolutionNew solution = readInput();
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
