package gui.mainscreen;

import java.io.IOException;
import domain.DCLoggedInUser;
import gui.login.LoginUserViewController;
import gui.manageContractType.ContractTypeScreenViewController;
import gui.manageCustomer.CustomersScreenViewController;
import gui.manageEmployee.EmployeesScreenViewController;
import gui.manageTicket.TicketScreenViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainScreenViewController extends HBox {

	@FXML
	private VBox navigation;
	@FXML
	private Button btnWelcome;
	@FXML
	private Button btnManageContracttypes;
	@FXML
	private Button btnManageTickets;
	@FXML
	private Button btnManageCustomers;
	@FXML
	private Button btnManageEmployees;
	@FXML
	private Button btnAccessKnowledgeBase;
	@FXML
	private Button btnStats;
	@FXML
	private Button btnLogOut;
	@FXML
	private StackPane content;

	public MainScreenViewController() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreenView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		// opmaak
		this.getStylesheets().add(getClass().getResource("/css/layout.css").toExternalForm());
		navigation.getStyleClass().add("navbar");
		content.getStyleClass().add("content");

		// fill content pane
		content.getChildren().add(new WelcomeScreenViewController());

		// knoppen in navigation worden verborgen, hangt af van role
		switch ((DCLoggedInUser.getUsermanager().getCurrentUser()).getRole()) {
		case Engineer:
			navigation.getChildren().remove(btnManageContracttypes);
			navigation.getChildren().remove(btnManageCustomers);
			navigation.getChildren().remove(btnManageEmployees);
			break;
		case SupportManager:
			navigation.getChildren().remove(btnManageCustomers);
			navigation.getChildren().remove(btnManageEmployees);
			break;
		case Administrator:
			navigation.getChildren().remove(btnManageContracttypes);
			navigation.getChildren().remove(btnManageTickets);
			navigation.getChildren().remove(btnAccessKnowledgeBase);
			navigation.getChildren().remove(btnStats);
			break;
		}
	}

	/**
	 * verander de getoonde content naar de meegegeven pane
	 */
	protected void changeContent(Pane pane) {
		content.getChildren().clear();
		content.getChildren().add(pane);
	}

	// Event Listener on Button[#btnWelcome].onAction
	@FXML
	private void welcomeButtonPushed(ActionEvent event) {
		changeContent(new WelcomeScreenViewController());
	}

	// Event Listener on Button[#btnManageContracts].onAction
	@FXML
	public void manageContracttypesButtonPushed(ActionEvent event) {
		changeContent(new ContractTypeScreenViewController());
	}

	// Event Listener on Button[#btnManageTickets].onAction
	@FXML
	public void manageTicketsButtonPushed(ActionEvent event) {
		changeContent(new TicketScreenViewController());
	}

	// Event Listener on Button[#btnManageCustomers].onAction
	@FXML
	public void manageCustomersButtonPushed(ActionEvent event) {
		changeContent(new CustomersScreenViewController());
	}

	// Event Listener on Button[#btnManageEmployees].onAction
	@FXML
	public void manageEmployeesButtonPushed(ActionEvent event) {
		changeContent(new EmployeesScreenViewController());
	}

	// Event Listener on Button[#btnAccessKnowledgeBase].onAction
	@FXML
	public void accessKnowledgeBaseButtonPushed(ActionEvent event) {
		changeContent(new WelcomeScreenViewController());
	}

	// Event Listener on Button[#btnStats].onAction
	@FXML
	public void statsButtonPushed(ActionEvent event) {
		changeContent(new WelcomeScreenViewController());
	}

	// Event Listener on Button[#btnLogOut].onAction
	@FXML
	private void logOutButtonPushed(ActionEvent event) {
		Scene scene = new Scene(new LoginUserViewController());
		Stage stage = (Stage) this.getScene().getWindow();
		stage.setScene(scene);
		stage.setTitle("Login Screen");
		stage.centerOnScreen();
		stage.show();
	}
}
