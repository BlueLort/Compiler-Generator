package view;
import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CodeAnalysisInfo {
    private Stage window;

    @FXML
    public TableView<InfoModel> dataTable ;
    @FXML
    public TableColumn<InfoModel, String> inputCol;
    @FXML
    public TableColumn<InfoModel, String> matchCol;
    @FXML
    public Label messageLabel;

    public void initialize(Stage primaryStage) {

        this.window = primaryStage;
        primaryStage.setTitle("Code Analysis");
        try {
            Parent root = FXMLLoader.load(new File("src/view/code-analysis.fxml").toURI().toURL());
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize(){
        inputCol.setCellValueFactory( new PropertyValueFactory("input"));
        matchCol.setCellValueFactory( new PropertyValueFactory("match"));
        messageLabel.setText("Auto-Saved to: bla bla bla");
    }

    public class InfoModel {

        private final String input;
        private final String match;

        private InfoModel(String input,String match) {
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
}
