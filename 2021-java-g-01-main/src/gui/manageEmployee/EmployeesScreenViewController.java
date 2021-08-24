package gui.manageEmployee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class EmployeesScreenViewController extends HBox {
	
	@FXML
	private StackPane navigation;
	@FXML
	private StackPane content;

	public EmployeesScreenViewController() {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesScreenView.fxml"));
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
		navigation.getChildren().add(new EmployeesSidebarController(this));
	}

	/**
	 * toont de geselecteerde employee, selectie gebeurt in navigatie links
	 */
	protected void changeContent(Pane pane) {
		content.getChildren().clear();
		content.getChildren().add(pane);
	}

}
