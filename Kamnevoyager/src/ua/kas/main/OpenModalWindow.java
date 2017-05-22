package ua.kas.main;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OpenModalWindow implements Runnable {

	private ActionEvent event = null;

	public OpenModalWindow(ActionEvent event) {
		this.event = event;
		run();
	}

	public void showDialog() throws Exception {
		Stage stage = new Stage();
		Parent root = FXMLLoader.load(this.getClass().getResource("Way.fxml"));
		stage.setTitle("Kamnevoyager");
		stage.setResizable(false);
		Scene scene = new Scene(root, 550 - 10, 250 - 10);
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.initModality(Modality.NONE);
		stage.initOwner(((Node) event.getSource()).getScene().getWindow());
		stage.getIcons().add(new Image(this.getClass().getResourceAsStream("Compass-icon.png")));
		stage.show();
	}

	@Override
	public void run() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					showDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
