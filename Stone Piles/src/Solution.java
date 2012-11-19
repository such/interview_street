import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Solution {

	private final static int MAX_PILE_SIZE = 50;

	public static class Game {
		GameStep initialGameStep;

		public Game(GameStep initialGameStep) {
			super();
			this.initialGameStep = initialGameStep;
		}

		public Player computeWinner() {
			if (initialGameStep.isWinningFromCache()) {
				return Player.ALICE;
			} else {
				return Player.BOB;
			}
		}
	}

	public static class GameStep implements Comparable<GameStep> {

		static class Cache {
			Map<GameStep, Boolean> gamesCache = new HashMap<GameStep, Boolean>();

			Set<GameStep> losingGamesCache = new TreeSet<Solution.GameStep>();

			public Cache() {
				GameStep singlePile1 = new GameStep(
						Collections.singletonList(new Pile(1)));
				GameStep singlePile2 = new GameStep(
						Collections.singletonList(new Pile(2)));
				GameStep singlePile4 = new GameStep(
						Collections.singletonList(new Pile(4)));
				GameStep singlePile8 = new GameStep(
						Collections.singletonList(new Pile(8)));

				addGame(singlePile1, Boolean.FALSE);
				addGame(singlePile2, Boolean.FALSE);
				addGame(singlePile4, Boolean.FALSE);
				addGame(singlePile8, Boolean.FALSE);

				for (int i = 9; i < 51; i++) {
					addGame(new GameStep(Collections.singletonList(new Pile(i))),
							Boolean.TRUE);
				}
				addGame(new GameStep(Collections.singletonList(new Pile(3))),
						Boolean.TRUE);
				addGame(new GameStep(Collections.singletonList(new Pile(5))),
						Boolean.TRUE);
				addGame(new GameStep(Collections.singletonList(new Pile(6))),
						Boolean.TRUE);
				addGame(new GameStep(Collections.singletonList(new Pile(7))),
						Boolean.TRUE);

				for (int i = 3; i < 51; i++) {
					for (int j = i + 1; j < 51; j++) {
						if (i != j && i != 4 && i != 8 && j != 4 && j != 8) {
							addGame(new GameStep(Arrays.asList(new Pile[] {
									new Pile(i), new Pile(j) })), Boolean.TRUE);
						}
					}
				}

			}

			public void addGame(GameStep gameStep, boolean winning) {
				gamesCache.put(gameStep, winning);
				if (!winning) {
					losingGamesCache.add(gameStep);
				}
			}

			public Boolean get(GameStep step) {
				return gamesCache.get(step);
			}

			public Set<GameStep> getLosingGames() {
				return losingGamesCache;
			}

		}

		static Cache cache = new Cache();

		List<Pile> piles;

		GameStep nextWinningStep;

		public GameStep(List<Pile> piles) {
			this.piles = new ArrayList<Pile>(piles);
			Collections.sort(this.piles, Collections.reverseOrder());
			this.piles = Collections.unmodifiableList(this.piles);
		}

		public static void precomputeSingleLosingPiles() {
			for (int pileSize = 3; pileSize <= 64; pileSize++) {
				System.out.println(pileSize);
				Pile pile = new Pile(pileSize);
				GameStep singlePileCase = new GameStep(
						Collections.singletonList(pile));
				cache.addGame(singlePileCase,
						singlePileCase.isWinningFromCache());
			}
		}

		boolean isWinning() {
			return reduceListOfPiles().isWinningFromCache();
		}

		boolean isWinningFromCache() {
			Boolean result = cache.get(this);
			if (result == null) {
				result = isReducedTestWinning();
				cache.addGame(this, result);
			}
			return result;
		}

		private boolean isReducedTestWinning() {
			if (piles.isEmpty()) {
				return false;
			}

			Set<GameStep> possibleSplits = getAllPossibleSplits();
			for (GameStep possibleSplit : possibleSplits) {
				if (!possibleSplit.isWinning()) {
					nextWinningStep = possibleSplit;
					return true;
				}
			}
			return false;
		}

		private Set<GameStep> getAllPossibleSplits() {
			Set<GameStep> splits = new HashSet<Solution.GameStep>();
			for (Pile pile : piles) {
				List<Pile> leftPiles = new ArrayList<Solution.Pile>(
						piles.size() - 1);
				for (Pile leftPile : piles) {
					if (leftPile != pile) {
						leftPiles.add(leftPile);
					}
				}
				for (GameStep possibleStep : pile
						.getAllSplitsWithoutIdentitySplit()) {
					List<Pile> possiblePiles = new ArrayList<Pile>(
							possibleStep.piles);
					possiblePiles.addAll(leftPiles);
					splits.add(new GameStep(possiblePiles));
				}
			}
			return splits;
		}

		private GameStep reduceListOfPiles() {
			return removeDoublons().removeLosingPiles();
		}

		private GameStep removeDoublons() {
			return new GameStep(removeDoublons(new ArrayList<Pile>(this.piles)));
		}

		private static List<Pile> removeDoublons(List<Pile> piles) {
			List<Pile> toRemove = new LinkedList<Pile>();
			for (int i = 0; i < piles.size() - 1; i++) {
				if (piles.get(i).equals(piles.get(i + 1))) {
					toRemove.add(piles.get(i));
					toRemove.add(piles.get(i + 1));
				}
			}
			piles.removeAll(toRemove);
			return piles;
		}

		private GameStep removeLosingPiles() {
			return new GameStep(removeLosingPiles(new ArrayList<Pile>(
					this.piles)));
		}

		private static List<Pile> removeLosingPiles(List<Pile> piles) {
			for (GameStep losingStep : cache.getLosingGames()) {
				if (piles.containsAll(losingStep.piles)) {
					piles.removeAll(losingStep.piles);
				}
			}

			return piles;
		}

		@Override
		public int compareTo(GameStep o) {
			if (!this.piles.isEmpty() && !o.piles.isEmpty()) {
				return this.piles.get(0).numberOfStones
						- o.piles.get(0).numberOfStones;
			} else {
				if (this.piles.isEmpty()) {
					return -1;
				} else {
					return 1;
				}
			}
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
			result = prime * result + ((piles == null) ? 0 : piles.hashCode());
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
			GameStep other = (GameStep) obj;
			if (piles == null) {
				if (other.piles != null)
					return false;
			} else if (!piles.equals(other.piles))
				return false;
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TestCase [piles=" + piles + "]";
		}
	}

	public static class Pile implements Comparable<Pile> {

		static final Map<Pile, Set<GameStep>> cacheWithIdentitySplit = new HashMap<Pile, Set<GameStep>>(
				MAX_PILE_SIZE);

		static final Map<Pile, Set<GameStep>> cacheWithoutIdentitySplit = new HashMap<Pile, Set<GameStep>>(
				MAX_PILE_SIZE);

		private final static Pile EMPTY_PILE = new Pile(0);

		final int numberOfStones;

		public Pile(int numberOfStones) {
			this.numberOfStones = numberOfStones;
		}

		public Set<GameStep> getAllSplitsWithoutIdentitySplit() {
			Set<GameStep> allSplitsWithoutIdentity = cacheWithoutIdentitySplit
					.get(this);
			if (allSplitsWithoutIdentity == null) {
				allSplitsWithoutIdentity = new HashSet<GameStep>(
						getAllSplitsIncludingIdentitySplit());
				allSplitsWithoutIdentity.remove(new GameStep(Collections
						.singletonList(this)));
				cacheWithoutIdentitySplit.put(this, allSplitsWithoutIdentity);
			}
			return allSplitsWithoutIdentity;
		}

		private Set<GameStep> getAllSplitsIncludingIdentitySplit() {
			Set<GameStep> possibleSplits = cacheWithIdentitySplit.get(this);
			if (possibleSplits == null) {
				possibleSplits = computeAllPossibleSplits();
				cacheWithIdentitySplit.put(this,
						Collections.unmodifiableSet(possibleSplits));
			}
			return possibleSplits;
		}

		private Set<GameStep> computeAllPossibleSplits() {
			if (numberOfStones <= 2) {
				return Collections.singleton(new GameStep(Collections
						.singletonList(this)));
			}

			Set<GameStep> possibleSplits = new HashSet<GameStep>();
			for (int firstPileStones = 1; firstPileStones <= numberOfStones; firstPileStones++) {

				Pile firstPile = new Pile(firstPileStones);
				Pile subPile = new Pile(numberOfStones - firstPileStones);
				for (GameStep gameStep : subPile
						.getAllSplitsIncludingIdentitySplit()) {
					if (!gameStep.piles.contains(firstPile)) {
						List<Pile> split = new ArrayList<Pile>(
								gameStep.piles.size() + 1);
						split.add(firstPile);
						split.addAll(gameStep.piles);
						split.remove(EMPTY_PILE);
						possibleSplits.add(new GameStep(split));
					}
				}
			}
			return possibleSplits;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Pile [numberOfStones=" + numberOfStones + "]";
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
			result = prime * result + numberOfStones;
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
			Pile other = (Pile) obj;
			if (numberOfStones != other.numberOfStones)
				return false;
			return true;
		}

		@Override
		public int compareTo(Pile o) {
			return numberOfStones - o.numberOfStones;
		}
	}

	private enum Player {
		ALICE, BOB;
	}

	private Set<Game> games;

	public Solution(Set<Game> games) {
		this.games = games;
	}

	public List<Player> getWinners() {
		List<Player> winners = new ArrayList<Player>(games.size());
		for (Game testCase : games) {
			winners.add(testCase.computeWinner());
		}
		return winners;
	}

	private static Set<Game> readInput() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = br.readLine();
		int nbTests = Integer.valueOf(s);
		Set<Game> games = new HashSet<Game>(nbTests);
		while (games.size() < nbTests) {
			int nbPiles = Integer.valueOf(br.readLine());
			List<Pile> piles = new ArrayList<Pile>(nbPiles);
			String[] pilesSizeString = br.readLine().split(" ");
			for (String pileSizeString : pilesSizeString) {
				piles.add(new Pile(Integer.valueOf(pileSizeString)));
			}
			games.add(new Game(new GameStep(piles)));
		}
		return games;
	}

	public static void main(String[] args) {
		try {
			Set<Game> testCases = readInput();
			Solution solution = new Solution(testCases);
			// GameStep.precomputeSingleLosingPiles();
			for (Player winner : solution.getWinners()) {
				System.out.println(winner);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}