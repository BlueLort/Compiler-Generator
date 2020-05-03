package view;

import java.io.File;
import java.nio.file.Paths;

import IOManagement.IOManager;
import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Compiler {

    private Stage window;

    @FXML
    private MenuItem clearResult;
    @FXML
    private MenuItem loadFile;

    @FXML
    private TextArea textArea;

    private Controller controller = new Controller();

    private String path;

    public void initialize(Stage primaryStage) {
        window = primaryStage;
        primaryStage.setTitle("Compiler");
        try {
            Parent root = FXMLLoader.load(new File("src/view/scene.fxml").toURI().toURL());
            Scene scene = new Scene(root, 1000, 750);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lexical Analyzer
    // -----------------------------------------------------------------------
    public void constructLexicalRulesOnAction() {
        if (textArea.getText().equalsIgnoreCase("")) {
            handleTextError("Empty text field !");
            return;
        }
        if (controller.constructLexicalRules(textArea.getText()) == false) {
            handleTextError("Wrong file format !");
        } else {
            endTask("DFA constructed successfully !");
        }

    }

    public void runCodeAnalysisOnAction() {
        if (textArea.getText().equalsIgnoreCase("")) {
            handleTextError("Empty text field !");
            return;
        }
        if (controller.runCodeAnalysisOnAction(textArea.getText()) == false) {
            handleTextError(
                    "Tokenization Errors \nNo DFA Constructed /\nSome symbols are unknown (specified with empty 'Match' field)!");
        }
    }

    // Parser
    // -----------------------------------------------------------------------
    public void loadLexemesOnAction() {
        FileChooser fileChooser = new FileChooser();
        setFileChooserOptions(fileChooser);
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            path = file.getAbsolutePath();
            controller.loadLexemesFromFile(path);
        }
    }

    public void constructParserRulesOnAction() {
        if (textArea.getText().equalsIgnoreCase("")) {
            handleTextError("Empty text field !");
            return;
        }
        if (controller.constructParserRules(textArea.getText()) == false) {
            handleTextError("Wrong file format or Tokens were not generated !");
        } else {
            endTask("Parsing Table constructed successfully!");
        }
    }

    public void parseInputOnAction() {
        if (controller.parseInput() == false) {// parse lexemes saved in tokenizer
            handleTextError("Error in Parsing the file no lexemes/parsing table defined or error during parsing phase !");
        }
    }

    /*
     * File Handling Methods including : Load file , Save file and clearing the text
     * area
     */

    public void loadFileOnAction() {

        FileChooser fileChooser = new FileChooser();
        setFileChooserOptions(fileChooser);

        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            path = file.getAbsolutePath();
            textArea.setText(IOManager.getInstance().readFile(path));
        }
    }

    public void saveAsOnAction() {
        FileChooser fileChooser = new FileChooser();
        setFileChooserOptions(fileChooser);
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            path = file.getAbsolutePath();
            IOManager.getInstance().writeFile(textArea.getText(), path);
        }
    }

    private void setFileChooserOptions(FileChooser fileChooser) {
        FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All", "*.*");
        FileChooser.ExtensionFilter extFilterTxt = new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt");
        FileChooser.ExtensionFilter extFilterJava = new FileChooser.ExtensionFilter("Java (*.java)", "*.java");
        fileChooser.getExtensionFilters().add(extFilterAll);
        fileChooser.getExtensionFilters().add(extFilterTxt);
        fileChooser.getExtensionFilters().add(extFilterJava);
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
    }

    public void saveOnAction() {
        if (path == null) {
            handleTextError("Path is undefined use `Save As` or `Load File` at least once !");
        }
        IOManager.getInstance().writeFile(textArea.getText(), path);
    }

    public void clearOnAction() {
        textArea.setText("");
    }

    /* error Handling Functions */

    private void handleTextError(String txt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("darkTheme.css").toExternalForm());
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }

    /* Helper function to be used at the end of the program */

    private void endTask(String message) {

        Alert alert = new Alert(AlertType.INFORMATION);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("darkTheme.css").toExternalForm());
        // dialogPane.getStyleClass().add("myDialog");
        alert.setTitle("Note");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}