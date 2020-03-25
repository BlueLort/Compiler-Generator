package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LexicalAnalyzer;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		LexicalAnalyzer assembler = new LexicalAnalyzer();
		assembler.Initialize(primaryStage);
	}
}