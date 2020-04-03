package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.Compiler;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		Compiler compiler = new Compiler();
		compiler.initialize(primaryStage);
	}
}