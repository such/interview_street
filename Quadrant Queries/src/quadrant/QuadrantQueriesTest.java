package quadrant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.testng.annotations.Test;

public class QuadrantQueriesTest {

	private static final int MAX_POINTS = 10000;
	private static final int MAX_QUERIES = 100000;

	// @DataProvider(name = "quadrantQueries")
	// public Object[][] createData() {
	@Test
	public void test() {
		int nbPoints = (int) (Math.random() * MAX_POINTS);
		int nbQueries = (int) (Math.random() * MAX_QUERIES);

		List<SolutionNew.PointWithCoordinates> points = new ArrayList<SolutionNew.PointWithCoordinates>(
				nbPoints);

		List<SolutionNew.Query> queries = new ArrayList<SolutionNew.Query>(
				nbQueries);

		for (int indice = 1; indice <= nbPoints; indice++) {
			int x = (int) ((Math.random() - 0.5) * Integer.MAX_VALUE);
			int y = (int) ((Math.random() - 0.5) * Integer.MAX_VALUE);
			points.add(new SolutionNew.PointWithCoordinates(indice, x, y));
		}

		for (int i = 0; i < nbQueries; i++) {
			double queryDouble = Math.random() * 3;
			SolutionNew.Query query;

			int p1 = (int) (Math.random() * nbPoints) + 1;
			int p2 = (int) (Math.random() * nbPoints) + 1;

			int first;
			int last;

			if (p1 < p2) {
				first = p1;
				last = p2;
			} else {
				first = p2;
				last = p1;
			}

			if (queryDouble < 1) {
				query = new SolutionNew.ReflectionX(
						new SolutionNew.Point(first), new SolutionNew.Point(
								last));
			} else if (queryDouble < 2) {
				query = new SolutionNew.ReflectionY(
						new SolutionNew.Point(first), new SolutionNew.Point(
								last));
			} else {
				query = new SolutionNew.Count(new SolutionNew.Point(first),
						new SolutionNew.Point(last));
			}
			queries.add(query);
		}
		// return new Object[][] { { points, queries } };
		//
		// }
		//
		// @Test(dataProvider = "quadrantQueries")
		// public void test(List<SolutionNew.PointWithCoordinates> points,
		// List<SolutionNew.Query> queries) {
		System.out.println("NB POINTS: " + points.size());
		System.out.println("NB QUERIES: " + queries.size());
		// for (SolutionNew.PointWithCoordinates point : points) {
		// System.out.println(point);
		// }
		// for (SolutionNew.Query query : queries) {
		// System.out.println(query);
		// }

		long time = System.currentTimeMillis();
		SolutionNew solution = new SolutionNew(points, queries);
		List<Map<SolutionNew.Quadrant, Integer>> compute = solution.compute();
		// for (Map<SolutionNew.Quadrant, Integer> result : compute) {
		// System.out.println(result.get(SolutionNew.Quadrant.NE) + " "
		// + result.get(SolutionNew.Quadrant.NO) + " "
		// + result.get(SolutionNew.Quadrant.SO) + " "
		// + result.get(SolutionNew.Quadrant.SE));
		// }
		System.out.println(System.currentTimeMillis() - time);

	}

	@Test
	public void testSubset() {
		TreeSet<Integer> set = new TreeSet<Integer>();
		for (int i = 0; i < 1000000; i++) {
			set.add((int) (Math.random() * 1000000));
		}
		for (int i = 0; i < 10; i++) {
			int firstIndex = (int) (Math.random() * 1000000);
			int lastIndex = firstIndex
					+ (int) (Math.random() * (10000000 - firstIndex));
			long time = System.nanoTime();
			int size = set.subSet(firstIndex, lastIndex).size();
			System.out.println("Time : " + (System.nanoTime() - time) + "\n"
					+ "firstIndex: " + firstIndex + "\n" + "lastIndex: "
					+ lastIndex + "\n" + "size: " + size);
		}
	}
}
