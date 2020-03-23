package view;

import java.io.File;
import java.nio.file.Paths;
import IOManagement.IOManager;
import controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LexicalAnalyzer {

    private Stage window;

    public MenuItem clearResult;
    public MenuItem loadFile;

    public CheckMenuItem restricted;
    public Label restrictedMsgLabel;

    public TextArea textArea;

    private Controller controller;

    private String path;

    public void Initialize(Stage primaryStage) {

        window = primaryStage;
        primaryStage.setTitle("Lexical Analyzer");

        try {
            Parent root = FXMLLoader.load(new File("src/view/scene.fxml").toURI().toURL());
            Scene scene = new Scene(root, 1000, 750);
            primaryStage.setScene(scene);
            primaryStage.show();
            controller = new Controller();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /* PLT Needed Operations */
    public void ConstructRulesOnAction(){
        if(textArea.getText().equalsIgnoreCase("")){
            HandleTextError("Empty text field !");
            return;
        }
        if(controller.ConstructRules(textArea.getText()) == false){
            HandleTextError("Wrong file format !");
        }
    }

    public void RunCodeAnalysisOnAction(){
        if(textArea.getText().equalsIgnoreCase("")){
            HandleTextError("Empty text field !");
            return;
        }
        if( controller.RunCodeAnalysisOnAction(textArea.getText()) == false) {
            HandleTextError("Failed to run code analysis!");
        }
    }





    /* File Handling Methods including : Load file , Save file and clearing the text area */

    public void LoadFileOnAction() {

        FileChooser fileChooser = new FileChooser();
        SetFileChooserOptions(fileChooser);

        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            path = file.getAbsolutePath();
            textArea.setText(IOManager.GetInstance().ReadFile(path));
        }
    }

    public void SaveAsOnAction() {
        FileChooser fileChooser = new FileChooser();
        SetFileChooserOptions(fileChooser);
        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            path = file.getAbsolutePath();
            IOManager.GetInstance().WriteFile(textArea.getText(), path);
        }
    }
    private void SetFileChooserOptions(FileChooser fileChooser){
        FileChooser.ExtensionFilter extFilterAll = new FileChooser.ExtensionFilter("All", "*.*");
        FileChooser.ExtensionFilter extFilterTxt = new FileChooser.ExtensionFilter("TXT (*.txt)", "*.txt");
        FileChooser.ExtensionFilter extFilterJava = new FileChooser.ExtensionFilter("Java (*.java)", "*.java");
        fileChooser.getExtensionFilters().add(extFilterAll);
        fileChooser.getExtensionFilters().add(extFilterTxt);
        fileChooser.getExtensionFilters().add(extFilterJava);
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
    }

    public void SaveOnAction() {
        if(path == null){
            HandleTextError("Path is undefined use `Save As` or `Load File` at least once !");
        }
        IOManager.GetInstance().WriteFile(textArea.getText(), path);
    }

    public void ClearOnAction() {
        textArea.setText("");
    }

    /* Helper functions */
    public void SetRestrictedMsg() {

        restrictedMsgLabel.setVisible(!restricted.isSelected());
    }

    /* error Handling Functions */

    private void HandleTextError(String txt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }

    /* Helper function to be used at the end of the program */

    private void EndTask(boolean noErrors) {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Note");
        alert.setHeaderText(null);
        String msg;
        msg = noErrors ? "Successful Operation" : "Incomplete Operation";
        alert.setContentText(msg);
        alert.showAndWait();
    }


}