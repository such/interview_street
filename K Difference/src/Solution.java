import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {

	List<Integer> numbers;
	int k;

	public Solution(List<Integer> numbers, int k) {
		super();
		this.numbers = numbers;
		Collections.sort(this.numbers);
		this.k = k;
	}

	private static Solution readInput() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		String[] nAndK = s.split(" ");
		int n = Integer.valueOf(nAndK[0]);
		int k = Integer.valueOf(nAndK[1]);

		s = br.readLine();
		String[] numbersS = s.split(" ");
		List<Integer> numbers = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			numbers.add(Integer.valueOf(numbersS[i]));
		}
		return new Solution(numbers, k);
	}

	public static void main(String[] args) {
		try {
			Solution solution = readInput();
			System.out.println(solution.computeNumberOfPairs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int computeNumberOfPairs() {
		int cursorMin = 0;
		int cursorMax = 1;
		int n = numbers.size();
		int result = 0;

		while (cursorMin < n && cursorMax < n) {
			int maxValue = numbers.get(cursorMax);
			int minValue = numbers.get(cursorMin);
			if (maxValue - minValue == k) {
				result++;
				cursorMax++;
				cursorMin++;
			} else if (maxValue - minValue < k) {
				cursorMax++;
			} else {
				if (cursorMax - cursorMin > 1) {
					cursorMin++;
				} else {
					cursorMax++;
					cursorMin++;
				}
			}
		}
		return result;
	}
}
