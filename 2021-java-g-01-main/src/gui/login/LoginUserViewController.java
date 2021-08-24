package gui.login;

import java.io.IOException;

import domain.DCLoginUser;
import gui.mainscreen.MainScreenViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class LoginUserViewController extends VBox {

	@FXML
	private TextField txfUserName;
	@FXML
	private PasswordField txfPassword;
	@FXML
	private Label lblMessage;

	private final DCLoginUser dcLoginUser;

	public LoginUserViewController() {
		this.dcLoginUser = new DCLoginUser();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginUserView.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
			this.getStylesheets().add(getClass().getResource("/css/layout.css").toExternalForm());
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	// Event Listener on Button[#btnSignUp].onAction
	@FXML
	public void btnSignInOnAction(ActionEvent event) {

		// aanroepen dcLoginUser voor verificatie, geeft een error message
		String message = dcLoginUser.verifyUser(txfUserName.getText(), txfPassword.getText());

		// als error message leeg is gaan we inloggen
		if (message.isEmpty()) {

			// als inloggen lukt gaan we naar de main screen
			Scene scene = new Scene(new MainScreenViewController());
			Stage stage = (Stage) this.getScene().getWindow();
			stage.setScene(scene);

			// main screen tittel instellen
			stage.setTitle(dcLoginUser.getCurrentUser().getRole().toString() + " " + txfUserName.getText());

			// maximize for cleanness, but re-login doesn't maximize automatically, so first
			// minimize to then re-maximize
			stage.setMaximized(false);
			stage.setMaximized(true);

			stage.show();
		}
		
		// fout in verificatie
		else {
			lblMessage.setText(message); // message voor domeinregels
			lblMessage.setTextFill(Color.color(1, 0, 0));
		}
	}

	// Event Listener on Button[#btnCancel].onAction
	@FXML
	public void btnCancelOnAction(ActionEvent event) {
		((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
	}

}
