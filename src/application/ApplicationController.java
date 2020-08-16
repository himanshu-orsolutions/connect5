package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import application.automations.ComputerMoveOptimizer;
import application.models.Move;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * The controller ApplicationController. It handles the events on main
 * application UI.
 */
public class ApplicationController implements Initializable {

	/**
	 * The grid pane (UI element)
	 */
	@FXML
	private GridPane gridPane;

	/**
	 * The computer tag label (UI element)
	 */
	@FXML
	private Label computerTagLabel;

	/**
	 * The player tag label (UI element)
	 */
	@FXML
	private Label playerTagLabel;

	/**
	 * The computer color label (UI element)
	 */
	@FXML
	private Label computerColorLabel;

	/**
	 * The player color label (UI element)
	 */
	@FXML
	private Label playerColorLabel;

	/**
	 * The computer moves label (UI element)
	 */
	@FXML
	private Label computerMovesLabel;

	/**
	 * The player moves label (UI element)
	 */
	@FXML
	private Label playerMovesLabel;

	/**
	 * The start button (UI element)
	 */
	@FXML
	private Button startButton;

	/**
	 * The play-pause button (UI element)
	 */
	@FXML
	private Button playPauseButton;

	/**
	 * The reset button (UI element)
	 */
	@FXML
	private Button resetButton;

	/**
	 * The save button (UI element)
	 */
	@FXML
	private Button saveButton;

	/**
	 * The total moves made by the computer
	 */
	private Integer computerMoves;

	/**
	 * The total moves made by the player
	 */
	private Integer playerMoves;

	/**
	 * The grid labels. Each label holds one color.
	 */
	private Circle[][] gridCircles;

	/**
	 * The array grid points. The grid point can be 0, 1, or 2. 0 indicates empty
	 * box, 1 indicates the move of computer, and 2 indicates the move of player.
	 */
	private int[][] gridPoints;

	/**
	 * If current turn is of computer
	 */
	private boolean computerTurn;

	/**
	 * If current turn is of player
	 */
	private boolean playerTurn;

	/**
	 * If the game is paused
	 */
	private boolean gamePaused;

	/**
	 * Shows the error message
	 * 
	 * @param error The error message
	 */
	private void showErrorMessage(String error) {

		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setContentText(error);
		errorAlert.showAndWait();
	}

	/**
	 * Shows the dialog message
	 * 
	 * @param dialog The dialog message
	 */
	private void showDialgMessage(String dialog) {

		Alert dialogAlert = new Alert(AlertType.INFORMATION);
		dialogAlert.setContentText(dialog);
		dialogAlert.showAndWait();
	}

	/**
	 * Resets the saved game file
	 */
	private void resetSavedGameFile() {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("saved.txt"))) {
			writer.write("");
		} catch (IOException ioException) {
			this.showErrorMessage("Error resetting the saved game file!");
		}
	}

	/**
	 * Shows the winner
	 * 
	 * @param winMoves The win moves
	 * @param player   The player
	 */
	private void showWinner(ArrayList<Move> winMoves, int player) {

		// The win moves will be highlighted in the grid by yellow color and the winner
		// will be shown in a separate dialog.
		winMoves.forEach(move -> gridCircles[move.getX()][move.getY()].setFill(Color.YELLOW));
		if (player == 1) {
			computerTurn = false;
			this.showDialgMessage("Computer wins!");
		} else {
			playerTurn = false;
			this.showDialgMessage("You win!");
		}
		saveButton.setDisable(true);
		playPauseButton.setDisable(true);
		resetSavedGameFile();
	}

	/**
	 * Evaluates the move.
	 */
	private ArrayList<Move> evaluateMove(int player) {

		// Note: The check is made after every move. All possible paths are checked to
		// check if any of computer or player made the winning move. The four directions
		// of evaluation are diagonal-up, horizontal-right, diagonal-down, and
		// vertical-down. These four directions will cover all cases.
		boolean win = false;
		ArrayList<Move> winMoves = new ArrayList<>();

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 8; j++) {

				if (i - 4 >= 0 && j + 4 < 8) { // Checking the diagonal up moves
					win = true;
					for (int m = 0; m < 5; m++) {
						if (gridPoints[i - m][j + m] != player) {
							win = false;
							break;
						}
					}

					if (win) {
						for (int m = 0; m < 5; m++) {
							winMoves.add(new Move(i - m, j + m));
						}
						return winMoves;
					}
				}

				if (j + 4 < 8) { // Checking the horizontal moves
					win = true;
					for (int m = 0; m < 5; m++) {
						if (gridPoints[i][j + m] != player) {
							win = false;
							break;
						}
					}

					if (win) {
						for (int m = 0; m < 5; m++) {
							winMoves.add(new Move(i, j + m));
						}
						return winMoves;
					}
				}

				if (i + 4 < 7 && j + 4 < 8) { // Checking the diagonal down moves
					win = true;
					for (int m = 0; m < 5; m++) {
						if (gridPoints[i + m][j + m] != player) {
							win = false;
							break;
						}
					}

					if (win) {
						for (int m = 0; m < 5; m++) {
							winMoves.add(new Move(i + m, j + m));
						}
						return winMoves;
					}
				}

				if (i + 4 < 7) { // Checking the vertical moves
					win = true;
					for (int m = 0; m < 5; m++) {
						if (gridPoints[i + m][j] != player) {
							win = false;
							break;
						}
					}

					if (win) {
						for (int m = 0; m < 5; m++) {
							winMoves.add(new Move(i + m, j));
						}
						return winMoves;
					}
				}
			}
		}

		return winMoves;
	}

	/**
	 * Delays for specified milliseconds
	 * 
	 * @param ms The milliseconds
	 */
	private void delay(long ms, Runnable task) {

		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(ms);
				Platform.runLater(task);
			} catch (InterruptedException interruptedException) {
				// do nothing
			}
		});
		thread.start();
	}

	/**
	 * Plays the computer move
	 */
	private void playComputerMove() {

		if (computerTurn) {
			Move nextMove = ComputerMoveOptimizer.getNextMove(gridPoints); // Gets the next move from
																			// ComputerMoveOptimizer
			if (nextMove != null) {
				gridCircles[nextMove.getX()][nextMove.getY()].setFill(Color.BLUE);
				gridPoints[nextMove.getX()][nextMove.getY()] = 1;
				computerMoves++;
				computerMovesLabel.setText(computerMoves.toString());
				computerTurn = false;
				playerTurn = true;
				ArrayList<Move> winMoves = this.evaluateMove(1);
				if (!winMoves.isEmpty()) {
					this.showWinner(winMoves, 1);
				}
			} else { // No next move found i.e. it's a draw
				this.showDialgMessage("It's a draw!");
			}
		}
	}

	/**
	 * Initiates a fresh start
	 */
	private void freshtart() {

		computerTurn = true;
		playerTurn = false;
		gamePaused = false;
		playerMoves = 0;
		computerMoves = 0;
		gridPoints = new int[7][8];
		gridCircles = new Circle[7][8];
		computerColorLabel.setStyle("-fx-background-color: blue");
		playerColorLabel.setStyle("-fx-background-color: red");
		computerMovesLabel.setText(computerMoves.toString());
		playerMovesLabel.setText(playerMoves.toString());
		computerTagLabel.setStyle("-fx-background-color: lightgrey");
		playerTagLabel.setStyle("-fx-background-color: lightgrey");
		playPauseButton.setDisable(true);
		resetButton.setDisable(true);
		startButton.setDisable(false);
		saveButton.setDisable(true);

		// Resetting the grid pane
		gridPane.getChildren().clear();

		double cellWidth = 450 / 8.0;
		double cellHeight = 390 / 7.0;

		double circleX = (cellWidth) / 2;
		double circleY = (cellHeight) / 2;
		double circleRadius = Math.sqrt(Math.pow(circleX, 2) + Math.pow(circleY, 2)) / 2;

		for (Integer i = 0; i < 7; i++) {
			for (Integer j = 0; j < 8; j++) {
				gridCircles[i][j] = new Circle(circleX, circleY, circleRadius);
				gridCircles[i][j].setFill(Color.LIGHTGREY);

				HBox box = new HBox(gridCircles[i][j]);
				box.setAlignment(Pos.CENTER);
				box.setPrefHeight(cellHeight);
				box.setPrefWidth(cellWidth);
				box.setStyle("-fx-background-color: black ;-fx-background-insets: 0, 0 0 1 1 ;");
				box.setOnMouseClicked(event -> {
					if (playerTurn) {
						int col = (int) (event.getSceneX() / cellWidth);
						int row = 0;
						boolean filled = true;
						for (int r = 6; r >= 0; r--) {
							if (gridPoints[r][col] == 0) {
								row = r;
								filled = false;
								break;
							}
						}

						if (filled) {
							this.showErrorMessage("Invalid move.");
						} else {
							gridCircles[row][col].setFill(Color.RED);
							gridPoints[row][col] = 2;
							playerMoves++;
							playerMovesLabel.setText(playerMoves.toString());
							playerTurn = false;
							computerTurn = true;

							ArrayList<Move> winMoves = this.evaluateMove(2);
							if (!winMoves.isEmpty()) {
								this.showWinner(winMoves, 2);
							}
							this.delay(1500l, () -> this.playComputerMove());

						}
					}
				});

				gridPane.add(box, j, i);
			}
		}

		gridPane.setDisable(true);
	}

	/**
	 * Shows the grid points on game
	 * 
	 * @param gridPoints The grid points
	 */
	private void showGridPoints(int[][] gridPoints) {

		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 8; j++) {
				if (gridPoints[i][j] == 1) {
					computerMoves++;
					gridCircles[i][j].setFill(Color.BLUE);
				} else if (gridPoints[i][j] == 2) {
					playerMoves++;
					gridCircles[i][j].setFill(Color.RED);
				}
			}
		}

		if (computerMoves > playerMoves) {
			playerTurn = true;
			computerTurn = false;
		} else {
			computerTurn = true;
			playerTurn = false;
		}

		computerMovesLabel.setText(computerMoves.toString());
		playerMovesLabel.setText(playerMoves.toString());
	}

	/**
	 * Resets/Initializes the game
	 */
	private void reset() {
		try {
			File savedGame = new File("saved.txt");
			if (savedGame.exists()) {
				String data = new String(Files.readAllBytes(Paths.get("saved.txt")));
				if ("".equals(data)) {
					freshtart();
				} else {
					Alert alert = new Alert(AlertType.INFORMATION, "Found saved game. Want to continue?", ButtonType.OK,
							ButtonType.CANCEL);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.get() == ButtonType.OK) {
						resetSavedGameFile();
						freshtart();
						String[] splitted = data.split(",");
						int count = 0;
						for (int i = 0; i < 7; i++) {
							for (int j = 0; j < 8; j++) {
								gridPoints[i][j] = Integer.parseInt(splitted[count++]);
							}
						}

						showGridPoints(gridPoints);
						onStart();
					} else {
						resetSavedGameFile();
						freshtart();
					}
				}
			} else {
				freshtart();
			}
		} catch (IOException ioException) {
			this.showErrorMessage("Error reading the saved game.");
			freshtart();
		}
	}

	/**
	 * Initializes the game
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.reset();
	}

	/**
	 * Triggers when start button is clicked
	 */
	@FXML
	public void onStart() {

		startButton.setDisable(true);
		playPauseButton.setDisable(false);
		resetButton.setDisable(false);
		gridPane.setDisable(false);
		saveButton.setDisable(false);
		this.playComputerMove();
	}

	/**
	 * Triggers when play/pause button is clicked
	 */
	@FXML
	public void onPlayPause() {

		if (gamePaused) {
			playPauseButton.setText("Pause");
			resetButton.setDisable(false);
			gridPane.setDisable(false);
			gamePaused = false;

		} else {
			gamePaused = true;
			gridPane.setDisable(true);
			resetButton.setDisable(true);
			playPauseButton.setText("Play");
		}
	}

	/**
	 * Triggers when reset button is clicked
	 */
	@FXML
	public void onReset() {

		this.reset();
		this.resetSavedGameFile();
	}

	/**
	 * Triggers when save button is clicked
	 */
	@FXML
	public void onSave() {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("saved.txt"))) {
			StringBuilder dataBuilder = new StringBuilder();
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 8; j++) {
					dataBuilder.append(gridPoints[i][j]);
					dataBuilder.append(",");
				}
			}
			writer.write(dataBuilder.toString());
		} catch (IOException ioException) {
			this.showErrorMessage("Error saving the game!");
		}
	}
}
