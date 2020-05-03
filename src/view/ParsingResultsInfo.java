package view;

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
import utilities.Constant;

import java.io.File;
import java.util.ArrayList;

public class ParsingResultsInfo {
    @FXML
    public TableView<InfoModelParsingResults> dataParserResultsTable;
    @FXML
    public TableColumn<InfoModelParsingResults, String> stackCol;
    @FXML
    public TableColumn<InfoModelParsingResults, String> inputCol;
    @FXML
    public TableColumn<InfoModelParsingResults, String> actionCol;

    @FXML
    public Label messageLabel;

    // Array list of :stack : input , action
    // the parsing table as specified in ParserGenerator
    public void initialize(ArrayList<Pair<String, Pair<String, String>>> parsingLog) {
        try {
            FXMLLoader loader = new FXMLLoader(new File("src/view/parsing-results.fxml").toURI().toURL());
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            ParsingResultsInfo newController = loader.getController();
            newController.showParsingResultsData(parsingLog);
            newController.saveStack(parsingLog);
            Stage stage = new Stage();
            stage.setTitle("Parsing Result");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Parser First Follow
        stackCol.setCellValueFactory(new PropertyValueFactory("stack"));
        inputCol.setCellValueFactory(new PropertyValueFactory("inputLexemes"));
        actionCol.setCellValueFactory(new PropertyValueFactory("action"));
    }

    private void saveStack(ArrayList<Pair<String, Pair<String, String>>> parsingLog){
        messageLabel.setText("Auto-Saved to: "+ Constant.PARSING_SAVING_PATH);
        //Transform the stack to single string then send it to the IOManager
        String out = "";
        for (Pair<String, Pair<String, String>> log : parsingLog) {
            out += log.getKey();
            out += System.lineSeparator();
        }
        IOManager.getInstance().writeFile(out,Constant.PARSING_SAVING_PATH);


    }
    private void showParsingResultsData(ArrayList<Pair<String, Pair<String, String>>> parsingLog) {
        ObservableList<InfoModelParsingResults> data = FXCollections.observableArrayList();
        for (Pair<String, Pair<String, String>> dataValue : parsingLog) {
            data.add(new InfoModelParsingResults(dataValue.getKey(), dataValue.getValue().getKey(), dataValue.getValue().getValue()));
        }
        dataParserResultsTable.setItems(data);
    }


    public class InfoModelParsingResults {

        private final String stack;
        private final String inputLexemes;
        private final String action;

        private InfoModelParsingResults(String stack, String inputLexemes, String action) {
            this.stack = stack;
            this.inputLexemes = inputLexemes;
            this.action = action;
        }

        public String getStack() {
            return stack;
        }

        public String getInputLexemes() {
            return inputLexemes;
        }

        public String getAction() {
            return action;
        }
    }
}
