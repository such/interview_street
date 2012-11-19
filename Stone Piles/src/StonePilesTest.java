import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StonePilesTest {

	@DataProvider(name = "pileSize")
	public Object[][] createPileSize() {
		return new Object[][] { { 1 }, { 2 }, { 4 }, { 12 }, { 25 }, { 50 } };
	}

	@Test(dataProvider = "pileSize")
	public void testGetAllPossibleSplits(int pileSize) {
		Solution.Pile pile = new Solution.Pile(pileSize);
		Set<Solution.GameStep> allPossibleSplits = pile
				.getAllSplitsWithoutIdentitySplit();
		System.out.println(pileSize + ": " + allPossibleSplits);
		for (Solution.GameStep testCase : allPossibleSplits) {
			Set<Solution.Pile> piles = new HashSet<Solution.Pile>(
					testCase.piles);
			AssertJUnit.assertEquals("there are doublons", piles.size(),
					testCase.piles.size());
			int totalSize = 0;
			for (Solution.Pile newPile : piles) {
				totalSize += newPile.numberOfStones;
			}
			AssertJUnit.assertEquals(pileSize, totalSize);
		}
	}

	@Test
	public void testPrecomputeLosingPiles() {
		long time = System.currentTimeMillis();
		Solution.GameStep.precomputeSingleLosingPiles();
		System.out.println(System.currentTimeMillis() - time);
	}

	@Test
	public void testIsWinningGame() {
		long time = System.currentTimeMillis();
		Solution.GameStep testCase = new Solution.GameStep(
				Arrays.asList(new Solution.Pile[] { new Solution.Pile(40),
						new Solution.Pile(36), new Solution.Pile(7) }));

		System.out.println(testCase);
		System.out.println(testCase.isWinningFromCache());
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(StringUtils.join(
				Solution.GameStep.cache.losingGamesCache, "\n"));
	}

	@Test
	public void testIsWinningGame2() {
		long time = System.currentTimeMillis();
		Solution.GameStep testCase = new Solution.GameStep(
				Arrays.asList(new Solution.Pile[] { new Solution.Pile(50),
						new Solution.Pile(49), new Solution.Pile(48) }));

		System.out.println(testCase);
		System.out.println(testCase.isWinningFromCache());
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(StringUtils.join(
				Solution.GameStep.cache.losingGamesCache, "\n"));
	}
}
