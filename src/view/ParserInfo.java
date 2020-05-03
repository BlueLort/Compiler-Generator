package view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import IOManagement.IOManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.lexical_analyzer.graph.Node;
import model.parser.parser.ParserGenerator;
import utilities.Constant;
import utilities.Utility;

public class ParserInfo {
    @FXML
    public TableView<InfoModelFirstFollow> dataParserFirstFollowTable;
    @FXML
    public TableColumn<InfoModelFirstFollow, String> nonterminalCol;
    @FXML
    public TableColumn<InfoModelFirstFollow, String> firstSetCol;
    @FXML
    public TableColumn<InfoModelFirstFollow, String> followSetCol;

    @FXML
    public GridPane parsingTableGrid;

    private ArrayList<ArrayList<Label>> data;

    @FXML
    public Label messageLabel;

    ParserGenerator parserGenerator;

    // Array list of :nonterminal : first , follow
    // the parsing table as specified in ParserGenerator
    public void initialize(ParserGenerator parserGenerator, ArrayList<String> terminals) {
        try {
            FXMLLoader loader = new FXMLLoader(new File("src/view/parser-analysis.fxml").toURI().toURL());
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            ParserInfo newController = loader.getController();
            ArrayList<Pair<String, Pair<String, String>>> firstFollowSets = getParsingFirstFollow(
                    parserGenerator.getNonTerminals(), parserGenerator.getFirstSet(), parserGenerator.getFollowSet());
            newController.showParsingData(firstFollowSets);
            newController.showParsingTable(terminals, firstFollowSets, parserGenerator.getParsingTable());
            Stage stage = new Stage();
            stage.setTitle("Parsing Analysis");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Parser First Follow
        nonterminalCol.setCellValueFactory(new PropertyValueFactory("nonterminal"));
        firstSetCol.setCellValueFactory(new PropertyValueFactory("firstSet"));
        followSetCol.setCellValueFactory(new PropertyValueFactory("followSet"));
    }

    private void showParsingData(ArrayList<Pair<String, Pair<String, String>>> parserFirstFollowSets) {
        ObservableList<InfoModelFirstFollow> data = FXCollections.observableArrayList();
        for (Pair<String, Pair<String, String>> dataValue : parserFirstFollowSets) {
            data.add(new InfoModelFirstFollow(dataValue.getKey(), dataValue.getValue().getKey(),
                    dataValue.getValue().getValue()));
        }
        dataParserFirstFollowTable.setItems(data);
    }

    private void showParsingTable(ArrayList<String> terminals,
                                  ArrayList<Pair<String, Pair<String, String>>> parserFirstFollowSets,
                                  HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable) {
        Integer currentRowCounter = 1;
        Integer currentColCounter = 1;
        HashMap<String, Integer> terminalLocation = new HashMap<>();
        HashMap<String, Integer> nonTerminalLocation = new HashMap<>();
        data = new ArrayList<>();
        ArrayList<Label> dataRow = new ArrayList<>();
        Label label = getLabel("");
        dataRow.add(label);
        data.add(dataRow);
        addToTable(label, 0, 0);

        // ADD nonterminals
        for (Pair<String, Pair<String, String>> set : parserFirstFollowSets) {
            dataRow = new ArrayList<>();
            label = getLabel(set.getKey());
            dataRow.add(label);
            data.add(dataRow);
            addToTable(label, 0, currentRowCounter);
            nonTerminalLocation.put(set.getKey(), currentRowCounter++);
        }
        // Fix dataRow Size
        for (int i = 0; i < data.size(); i++) {
            for (int j = data.get(i).size() - 1; j < terminals.size(); j++) {
                label = getLabel("");
                data.get(i).add(label);
                addToTable(label, j + 1, i);
            }
        }

        // edit added Nodes for each terminal and then for each output
        for (String terminal : terminals) {
            // data.get(0).get(currentColCounter).setStyle("");//TODO EDIT STYLE
            data.get(0).get(currentColCounter).setText(terminal);
            terminalLocation.put(terminal, currentColCounter++);
        }

        // Set Values of table
        for (Pair<String, Pair<String, String>> set : parserFirstFollowSets) {
            int rowLoc = nonTerminalLocation.get(set.getKey());
            for (String terminal : terminals) {
                if (parsingTable.get(set.getKey()).containsKey(terminal)) {
                    int colLoc = terminalLocation.get(terminal);
                    String locValue = "";
                    for (ArrayList<String> production : parsingTable.get(set.getKey()).get(terminal)) {
                        locValue += Utility.getStringFromArrayList(production);
                        locValue += Constant.PARSING_TABLE_SEPARATOR;
                    }
                    locValue = locValue.substring(0, locValue.length() - Constant.PARSING_TABLE_SEPARATOR.length()); // remove
                    // the
                    // separator
                    data.get(rowLoc).get(colLoc).setStyle("");// remove styling
                    data.get(rowLoc).get(colLoc).setText(locValue);
                }
            }
        }

        // Fix Table rows and column size
        final int numCols = currentColCounter;
        final int numRows = currentRowCounter - 1;
        fixTableContraint(numCols, numRows);

    }

    public class InfoModelFirstFollow {

        private final String nonterminal;
        private final String firstSet;
        private final String followSet;

        private InfoModelFirstFollow(String nonterminal, String firstSet, String followSet) {
            this.nonterminal = nonterminal;
            this.firstSet = firstSet;
            this.followSet = followSet;
        }

        public String getNonterminal() {
            return nonterminal;
        }

        public String getFirstSet() {
            return firstSet;
        }

        public String getFollowSet() {
            return followSet;
        }
    }

    private ArrayList<Pair<String, Pair<String, String>>> getParsingFirstFollow(ArrayList<String> nonterminals,
                                                                                HashMap<String, HashSet<String>> firstSet, HashMap<String, HashSet<String>> followSet) {
        ArrayList<Pair<String, Pair<String, String>>> out = new ArrayList<>();
        for (String nonterminal : nonterminals) {
            out.add(new Pair<>(nonterminal, new Pair<>(Utility.getStringFromHashSet(firstSet.get(nonterminal)),
                    Utility.getStringFromHashSet(followSet.get(nonterminal)))));
        }
        return out;
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        label.setStyle(" -fx-prompt-text-fill: derive(-fx-control-inner-background, +25%);\n"
                + "    -fx-font-size: 14pt;\n" + "    -fx-font-weight: 700;\n" + "    -fx-font-family: monospace;\n"
                + "    -fx-text-fill: #ffffff;\n" + "    -fx-opacity: 1.0;");
        return label;
    }

    private void addToTable(Label label, int x, int y) {
        this.parsingTableGrid.add(label, x, y, 1, 1);
        parsingTableGrid.setFillWidth(label, true);
        parsingTableGrid.setFillHeight(label, true);

    }

    private void fixTableContraint(int numCols, int numRows) {
        final double defaultSize = 500.0;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(defaultSize / numCols);
            this.parsingTableGrid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(defaultSize / numRows);
            this.parsingTableGrid.getRowConstraints().add(rowConst);
        }
    }
}
