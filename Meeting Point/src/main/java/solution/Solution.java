package solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {

	Set<House> houses;

	long minDistance = Long.MAX_VALUE;

	public static class House {
		long x;
		long y;

		long nx;
		long ny;

		long distanceVariation;

		Map<House, Long> distancesWithHousesCache = new HashMap<House, Long>();

		public House(long x, long y) {
			this.x = x;
			this.y = y;

			this.nx = x - y;
			this.ny = x + y;
		}

		public long distanceWith(House house) {
			Long distance = distancesWithHousesCache.get(house);
			if (distance == null) {
				distance = Math.max(Math.abs(house.x - x),
						Math.abs(house.y - y));
				distancesWithHousesCache.put(house, distance);
			}
			return distance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "House [x=" + x + ", y=" + y + "]";
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
			result = prime * result + (int) (x ^ (x >>> 32));
			result = prime * result + (int) (y ^ (y >>> 32));
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
			House other = (House) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	public Solution(Set<House> houses) {
		this.houses = houses;
	}

	public long findMeetingPlaceWithBruteForce() {

		long sumOfDistance = minDistance;

		for (House meetingPlace : houses) {
			sumOfDistance = sumOfDistance(meetingPlace);
			if (sumOfDistance < minDistance) {
				minDistance = sumOfDistance;
			}
		}

		return minDistance;
	}

	public long findMeetingPlace() {

		List<House> housesOnX = new ArrayList<House>(houses);
		Collections.sort(housesOnX, new Comparator<House>() {
			public int compare(House house1, House house2) {
				long diff = house1.nx - house2.nx;
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		List<House> housesOnY = new ArrayList<House>(houses);
		Collections.sort(housesOnY, new Comparator<House>() {
			public int compare(House house1, House house2) {
				long diff = house1.ny - house2.ny;
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		int size = houses.size();

		if (size % 2 == 0) {
			long deltaX = 0;
			long deltaY = 0;
			long factor = 0;
			for (int i = size / 2; i > 1; i--) {
				housesOnX.get(i - 1).distanceVariation += deltaX;
				housesOnY.get(i - 1).distanceVariation += deltaY;

				factor++;
				deltaX += 2 * factor
						* (housesOnX.get(i - 1).nx - housesOnX.get(i - 2).nx);
				deltaY += 2 * factor
						* (housesOnY.get(i - 1).ny - housesOnY.get(i - 2).ny);
			}
			housesOnX.get(0).distanceVariation += deltaX;
			housesOnY.get(0).distanceVariation += deltaY;

			deltaX = 0;
			deltaY = 0;
			factor = 0;
			for (int i = size / 2 + 1; i < size; i++) {
				housesOnX.get(i - 1).distanceVariation += deltaX;
				housesOnY.get(i - 1).distanceVariation += deltaY;

				factor++;
				deltaX += 2 * factor
						* (housesOnX.get(i).nx - housesOnX.get(i - 1).nx);
				deltaY += 2 * factor
						* (housesOnY.get(i).ny - housesOnY.get(i - 1).ny);
			}
			housesOnX.get(size - 1).distanceVariation += deltaX;
			housesOnY.get(size - 1).distanceVariation += deltaY;
		} else {
			long deltaX = 0;
			long deltaY = 0;
			long factor = 1;
			for (int i = (size - 1) / 2; i > 0; i--) {
				deltaX += factor
						* (housesOnX.get(i).nx - housesOnX.get(i - 1).nx);
				deltaY += factor
						* (housesOnY.get(i).ny - housesOnY.get(i - 1).ny);

				housesOnX.get(i - 1).distanceVariation += deltaX;
				housesOnY.get(i - 1).distanceVariation += deltaY;
				factor += 2;

			}

			deltaX = 0;
			deltaY = 0;
			factor = 1;
			for (int i = (size + 3) / 2; i <= size; i++) {
				deltaX += factor
						* (housesOnX.get(i - 1).nx - housesOnX.get(i - 2).nx);
				deltaY += factor
						* (housesOnY.get(i - 1).ny - housesOnY.get(i - 2).ny);

				housesOnX.get(i - 1).distanceVariation += deltaX;
				housesOnY.get(i - 1).distanceVariation += deltaY;
				factor += 2;
			}
		}

		Long minVariation = housesOnX.get(0).distanceVariation;
		House meetingHouse = housesOnX.get(0);
		for (House house : houses) {
			if (house.distanceVariation < minVariation) {
				meetingHouse = house;
				minVariation = house.distanceVariation;
			}
		}
		long distance = 0;
		for (House house : houses) {
			distance += meetingHouse.distanceWith(house);
		}
		return distance;

	}

	protected long sumOfDistance(House meetingPlace) {
		long result = 0;
		for (House house : houses) {
			result += house.distanceWith(meetingPlace);
			if (result > minDistance) {
				return result;
			}
		}
		return result;
	}

	private static Set<House> readInput() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		int numberOfHouses = Integer.valueOf(s);
		Set<House> houses = new HashSet<House>(numberOfHouses);
		while (houses.size() < numberOfHouses) {
			s = br.readLine();
			String[] coordinateString = s.split(" ");
			House house = new House(Long.valueOf(coordinateString[0]),
					Long.valueOf(coordinateString[1]));
			houses.add(house);
		}
		return houses;
	}

	public static void main(String[] args) {
		try {
			Set<House> houses = readInput();
			Solution solution = new Solution(houses);
			System.out.println(solution.findMeetingPlace());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
