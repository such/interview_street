package solution;

import java.util.HashSet;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import solution.Solution.House;

public class MeetingPoint {

	@DataProvider(name = "houses")
	public Object[][] createData() {
		// int nbHouse = (int) (Math.random() * 100000);
		//
		// Set<House> houses0 = new HashSet<House>(nbHouse);
		// while (houses0.size() < nbHouse) {
		// int x = (int) (Math.random() * 1000000000);
		// int y = (int) (Math.random() * 1000000000);
		// houses0.add(new House(x, y));
		// }

		Set<House> houses0 = new HashSet<House>(3);
		houses0.add(new House(0, 0));
		houses0.add(new House(-1, 0));
		houses0.add(new House(1, 0));

		Set<House> houses1 = new HashSet<House>(4);
		houses1.add(new House(0, 1));
		houses1.add(new House(2, 5));
		houses1.add(new House(3, 1));
		houses1.add(new House(4, 0));

		Set<House> houses2 = new HashSet<House>(6);
		houses2.add(new House(12, -14));
		houses2.add(new House(-3, 3));
		houses2.add(new House(-14, 7));
		houses2.add(new House(-14, -3));
		houses2.add(new House(2, -12));
		houses2.add(new House(-1, -6));

		return new Object[][] { { houses0, 2 }, { houses1, 8 }, { houses2, 54 } };
	}

	@Test(dataProvider = "houses")
	public void test(Set<House> houses, long result) {

		// int nbHouse = (int) (Math.random() * 100000);
		//
		// Set<House> houses = new HashSet<House>(nbHouse);
		// while (houses.size() < nbHouse) {
		// int x = (int) (Math.random() * 1000000000);
		// int y = (int) (Math.random() * 1000000000);
		// houses.add(new House(x, y));
		// }

		Solution solution = new Solution(houses);
		System.out.println("SIZE: " + houses.size());
		long time = System.nanoTime();
		AssertJUnit.assertEquals(result, solution.findMeetingPlace());
		System.out.println(solution.findMeetingPlace());
		System.out.println("NEW: " + (System.nanoTime() - time));

		// time = System.nanoTime();
		// solution.findMeetingPlaceOld();
		// AssertJUnit.assertEquals(result, solution.findMeetingPlaceOld());
		// System.out.println("OLD: " + (System.nanoTime() - time));
	}

}
