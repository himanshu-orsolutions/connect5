package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	/**
	 * Starts the application
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ConnectFive.fxml")); // The UI components are configured in
																			// 'ConnectFive.fxml'
			Pane root = loader.load();
			Scene scene = new Scene(root, 600, 390);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Connect5");
			primaryStage.show();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Execution starts from here
	 * 
	 * @param args The command line argument
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
