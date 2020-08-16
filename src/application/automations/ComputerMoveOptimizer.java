package application.automations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import application.models.Move;

public class ComputerMoveOptimizer {

	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private ComputerMoveOptimizer() {
		// Its a utility class
	}

	/**
	 * Gets the count of negative moves
	 * 
	 * @param path The path
	 * @return The count of negative moves
	 */
	private static int getNegativeMoveCount(List<Move> path, int[][] gridPoints) {

		int count = 0;
		for (Move move : path) {
			int point = gridPoints[move.getX()][move.getY()];
			if (point == 2) {
				count++;
			} else if (point == 1) {
				count = 0;
				break;
			}
		}
		return count;
	}

	/**
	 * Gets the count of positive moves
	 * 
	 * @param path The path
	 * @return The count of positive moves
	 */
	private static int getPositveMoveCount(List<Move> path, int[][] gridPoints) {

		int count = 0;
		for (Move move : path) {
			int point = gridPoints[move.getX()][move.getY()];
			if (point == 1) {
				count++;
			} else if (point == 2) {
				count = 0;
				break;
			} else {
				if (move.getX() != 6 && gridPoints[move.getX() + 1][move.getY()] == 0) {
					return 0;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the best move
	 * 
	 * @param gridPoints The grid points
	 * @return The best move
	 */
	public static Move getBestMove(int[][] gridPoints) {

		// Note: The best path is the one which either has max moves from opponent or
		// max moves from computer. In first case, the computer will try to block the
		// path of opponent.The four directions of evaluation are diagonal-up,
		// horizontal-right, diagonal-down, and vertical-down. These four directions
		// will cover all cases.
		int maxPositiveCount = 0;
		int maxNegativeCount = 0;

		ArrayList<Move> choosenPath = new ArrayList<>();

		for (int i = 6; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				if (i - 4 >= 0 && j + 4 < 8) { // Checking the diagonal up-right moves
					ArrayList<Move> path = new ArrayList<>();
					for (int m = 0; m < 5; m++) {
						path.add(new Move(i - m, j + m));
					}

					int negativeCount = getNegativeMoveCount(path, gridPoints);
					int positiveCount = getPositveMoveCount(path, gridPoints);

					if (positiveCount > maxNegativeCount && positiveCount > maxPositiveCount) {
						maxPositiveCount = positiveCount;
						choosenPath = path;
					} else if (negativeCount > maxPositiveCount && negativeCount > maxNegativeCount) {
						maxNegativeCount = negativeCount;
						choosenPath = path;
					}
				}

				if (j + 4 < 8) { // Checking the horizontal moves
					ArrayList<Move> path = new ArrayList<>();
					for (int m = 0; m < 5; m++) {
						path.add(new Move(i, j + m));
					}

					int negativeCount = getNegativeMoveCount(path, gridPoints);
					int positiveCount = getPositveMoveCount(path, gridPoints);

					if (positiveCount > maxNegativeCount && positiveCount > maxPositiveCount) {
						maxPositiveCount = positiveCount;
						choosenPath = path;
					} else if (negativeCount > maxPositiveCount && negativeCount > maxNegativeCount) {
						maxNegativeCount = negativeCount;
						choosenPath = path;
					}
				}

				if (i - 4 >= 0 && j - 4 >= 0) { // Checking the diagonal up-left moves
					ArrayList<Move> path = new ArrayList<>();
					for (int m = 0; m < 5; m++) {
						path.add(new Move(i - m, j - m));
					}

					int negativeCount = getNegativeMoveCount(path, gridPoints);
					int positiveCount = getPositveMoveCount(path, gridPoints);

					if (positiveCount > maxNegativeCount && positiveCount > maxPositiveCount) {
						maxPositiveCount = positiveCount;
						choosenPath = path;
					} else if (negativeCount > maxPositiveCount && negativeCount > maxNegativeCount) {
						maxNegativeCount = negativeCount;
						choosenPath = path;
					}
				}

				if (i - 4 >= 0) { // Checking the vertical up moves
					ArrayList<Move> path = new ArrayList<>();
					for (int m = 0; m < 5; m++) {
						path.add(new Move(i - m, j));
					}

					int negativeCount = getNegativeMoveCount(path, gridPoints);
					int positiveCount = getPositveMoveCount(path, gridPoints);

					if (positiveCount > maxNegativeCount && positiveCount > maxPositiveCount) {
						maxPositiveCount = positiveCount;
						choosenPath = path;
					} else if (negativeCount > maxPositiveCount && negativeCount > maxNegativeCount) {
						maxNegativeCount = negativeCount;
						choosenPath = path;
					}
				}
			}
		}

		if (!choosenPath.isEmpty()) {
			for (Move move : choosenPath) {
				if (move.getX() == 6) {
					if (gridPoints[move.getX()][move.getY()] == 0) {
						return move;
					}
				} else {
					if (gridPoints[move.getX() + 1][move.getY()] != 0 && gridPoints[move.getX()][move.getY()] == 0) {
						return move;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the next move
	 * 
	 * @param gridPoints The grid points
	 * @return The next move
	 */
	public static Move getNextMove(int[][] gridPoints) {

		// Gets the best move
		Move bestMove = getBestMove(gridPoints);

		if (bestMove != null) {
			return bestMove;
		} else { // No best path found i.e. there is no chance of win now. Thus the moves will
					// only fill the board now randomly.
			ArrayList<Move> leftMoves = new ArrayList<>();
			for (int i = 0; i < 8; i++) {
				for (int j = 6; j >= 0; j--) {
					if (gridPoints[j][i] == 0) {
						leftMoves.add(new Move(j, i));
						break;
					}
				}
			}

			if (!leftMoves.isEmpty()) {
				return leftMoves.get(RANDOM.nextInt(leftMoves.size()));
			}
		}

		return null; // Its a draw
	}
}
