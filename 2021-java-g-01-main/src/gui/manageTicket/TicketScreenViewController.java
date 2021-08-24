package gui.manageTicket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class TicketScreenViewController extends HBox {
	@FXML
	private StackPane navigation;
	@FXML
	private StackPane content;

	public TicketScreenViewController() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketScreenView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// opmaak
		navigation.getStyleClass().add("navbar");
		content.getStyleClass().add("content");

		// fill navigation pane
		navigation.getChildren().add(new TicketsSidebarController(this));
	}

	/**
	 * toont het geselecteerde ticket, selectie gebeurt in navigatie links
	 */
	protected void changeContent(Pane pane) {
		content.getChildren().clear();
		content.getChildren().add(pane);
	}

}
