package gui.mainscreen;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

public class WelcomeScreenViewController extends VBox {

	public WelcomeScreenViewController() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreenView.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
