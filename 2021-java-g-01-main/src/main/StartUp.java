package main;

import gui.login.LoginUserViewController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StartUp extends Application {

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(new LoginUserViewController());
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("/images/actemiumSmallLogo.jpg"));
		primaryStage.setTitle("Login Screen");

		primaryStage.show();
		
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
