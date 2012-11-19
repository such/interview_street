package solution;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Solution {

	static class Friend {
		int numberOfFlowersBought = 0;

		int buyFlower(Flower flower) {
			numberOfFlowersBought++;
			return numberOfFlowersBought * flower.price;
		}
	}

	static class Flower implements Comparable<Flower> {

		public Flower(int price) {
			this.price = price;
		}

		int price;

		@Override
		public int compareTo(Flower flower) {
			return price - flower.price;
		}
	}

	List<Flower> flowers;
	List<Friend> friends;
	int moneySpent;

	public Solution(List<Flower> flowers, int numberOfFriends) {
		this.flowers = flowers;
		friends = new ArrayList<Friend>(numberOfFriends);
		while (friends.size() < numberOfFriends) {
			friends.add(new Friend());
		}
	}

	public static void main(String[] args) {
		InputStream input = System.in;

		Scanner scanner = new Scanner(input);
		int numberOfFlowers = scanner.nextInt();
		int numberOfFriends = scanner.nextInt();

		List<Flower> flowers = new ArrayList<Flower>(numberOfFlowers);

		while (flowers.size() < numberOfFlowers) {
			flowers.add(new Flower(scanner.nextInt()));
		}

		Solution solution = new Solution(flowers, numberOfFriends);
		solution.compute();
		solution.outputResult();
	}

	private void outputResult() {
		System.out.println(moneySpent);
	}

	private void compute() {
		Collections.sort(flowers, Collections.reverseOrder());
		int flowersBought = 0;
		while (!flowers.isEmpty()) {
			Friend nextBuyer = friends.get(flowersBought % friends.size());
			moneySpent += nextBuyer.buyFlower(flowers.remove(0));
			flowersBought++;
		}
	}
}
