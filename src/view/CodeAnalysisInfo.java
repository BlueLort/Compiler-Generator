package view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import IOManagement.IOManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.lexical_analyzer.graph.Node;

public class CodeAnalysisInfo {
	private final String SAVING_PATH = "output/lexemes.txt";
	@FXML
	public TableView<InfoModelLexemes> dataLexemesTable;
	@FXML
	public TableColumn<InfoModelLexemes, String> inputCol;
	@FXML
	public TableColumn<InfoModelLexemes, String> matchCol;

	@FXML
	public TableView<InfoModelTransitionTable> dataTransitionTable;
	@FXML
	public TableColumn<InfoModelTransitionTable, String> sourceNodeCol;
	@FXML
	public TableColumn<InfoModelTransitionTable, String> nodeInputCol;
	@FXML
	public TableColumn<InfoModelTransitionTable, String> destinationNodeCol;
	@FXML
	public TableColumn<InfoModelTransitionTable, String> possibleOutCol;

	@FXML
	public Label messageLabel;

	public void initialize(ArrayList<Pair<String, String>> lexemes,
			HashMap<String, Pair<Node, String>> transitionTable) {
		try {
			FXMLLoader loader = new FXMLLoader(new File("src/view/code-analysis.fxml").toURI().toURL());
			Parent root = loader.load();
			Scene scene = new Scene(root, 800, 600);
			CodeAnalysisInfo newController = loader.getController();
			newController.saveFile(lexemes);
			newController.showLexemesData(lexemes);
			newController.showTransitionTableData(transitionTable);
			Stage stage = new Stage();
			stage.setTitle("Code Analysis");
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {
		// LEXEMS
		inputCol.setCellValueFactory(new PropertyValueFactory("input"));
		matchCol.setCellValueFactory(new PropertyValueFactory("match"));
		// TRANSITION TABLE
		sourceNodeCol.setCellValueFactory(new PropertyValueFactory("srcNodeID"));
		nodeInputCol.setCellValueFactory(new PropertyValueFactory("srcInput"));
		destinationNodeCol.setCellValueFactory(new PropertyValueFactory("dstNodeID"));
		possibleOutCol.setCellValueFactory(new PropertyValueFactory("dstOutput"));

	}

	private void saveFile(ArrayList<Pair<String, String>> lexemes) {
		messageLabel.setText("Auto-Saved to: " + SAVING_PATH);
		String out = "";
		for (Pair<String, String> lexeme : lexemes) {
			out += lexeme.getValue();
			out += System.lineSeparator();
		}
		IOManager.getInstance().writeFile(out, SAVING_PATH);
	}

	private void showLexemesData(ArrayList<Pair<String, String>> lexemes) {
		ObservableList<InfoModelLexemes> data = FXCollections.observableArrayList();
		for (Pair<String, String> lexeme : lexemes) {
			data.add(new InfoModelLexemes(lexeme.getKey(), lexeme.getValue()));
		}
		dataLexemesTable.setItems(data);
	}

	private void showTransitionTableData(HashMap<String, Pair<Node, String>> transitionTable) {
		ObservableList<InfoModelTransitionTable> data = FXCollections.observableArrayList();
		for (Map.Entry<String, Pair<Node, String>> entry : transitionTable.entrySet()) {
			String srcInput[] = entry.getKey().split(" ");
			data.add(new InfoModelTransitionTable(srcInput[0] // src node id
					, srcInput[1] // input for src node
					, Integer.toString(entry.getValue().getKey().getCurrentId()) // destination node id
					, entry.getValue().getValue() // output on going to that destination node
			));
		}
		dataTransitionTable.setItems(data);
	}

	public class InfoModelLexemes {

		private final String input;
		private final String match;

		private InfoModelLexemes(String input, String match) {
			this.input = input;
			this.match = match;
		}

		public String getInput() {
			return input;
		}

		public String getMatch() {
			return match;
		}
	}

	public class InfoModelTransitionTable {

		private final String srcNodeID;
		private final String dstNodeID;
		private final String srcInput;
		private final String dstOutput;

		private InfoModelTransitionTable(String srcNodeID, String srcInput, String dstNodeID, String dstOutput) {
			this.srcNodeID = srcNodeID;
			this.srcInput = srcInput;
			this.dstNodeID = dstNodeID;
			this.dstOutput = dstOutput;
		}

		public String getSrcNodeID() {
			return srcNodeID;
		}

		public String getDstNodeID() {
			return dstNodeID;
		}

		public String getSrcInput() {
			return srcInput;
		}

		public String getDstOutput() {
			return dstOutput;
		}
	}

}
