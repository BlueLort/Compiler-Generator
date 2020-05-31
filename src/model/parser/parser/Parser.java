package model.parser.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javafx.util.Pair;
import model.parser.cfg.CFG;
import utilities.Constant;

public class Parser {
    private HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable;
    /**
     * key of outer hashmap is a non terminal entry key of inner hashmap is terminal
     * char input ArrayList value of inner hashmap is set of rules (in case of
     * ambiguous) inner ArrayList is the production
     */

    private CFG grammar;

    private boolean errorFree = true;

    private ParsingTreeNode parsingTree;

    private ArrayList<String> inputTokens;


    private ArrayList<Pair<String, Pair<String, String>>> log;

    /**
     * <stack contents< remaining inputs , action >>
     */

    public Parser(ParserGenerator parserGenerator, ArrayList<Pair<String, String>> inputTokens) {
        this.parsingTree = new ParsingTreeNode();
        this.parsingTable = parserGenerator.getParsingTable();
        this.inputTokens = getInputTokens(inputTokens);
        this.grammar = parserGenerator.getGrammar();
        log = new ArrayList<>();
        parse();
    }

    private ArrayList<String> getInputTokens(ArrayList<Pair<String, String>> inputTokens) {
        ArrayList<String> inputTokensOut = new ArrayList<>();
        for (Pair<String, String> valLex : inputTokens) {
            inputTokensOut.add(valLex.getValue());
        }
        inputTokensOut.add(Constant.END_MARKER);
        return inputTokensOut;
    }

    public void parse() {
        int inputTokenIndex = 0;
        Stack<ParsingTreeNode> stack = new Stack<>();
        stack.push(new ParsingTreeNode(Constant.END_MARKER));

        parsingTree.setName(grammar.getStartingNonTerminal());
        ParsingTreeNode currNode = new ParsingTreeNode();

        stack.push(parsingTree);
        while (!stack.empty() && inputTokenIndex != inputTokens.size()) {

            Pair<String, Pair<String, String>> logEntry;

            StringBuilder stackContent = new StringBuilder();
            for (ParsingTreeNode node : stack) {
                stackContent.append(node.getName() + " ");
            }

            StringBuilder inputContents = new StringBuilder();
            for (int j = inputTokenIndex; j < inputTokens.size(); j++) {
                inputContents.append(inputTokens.get(j) + " ");
            }


            ParsingTreeNode TOS = stack.pop();

            currNode = TOS;

            if (grammar.isNonTerminal(TOS.getName())) { /** if top of stack is non terminal */
                /** if top of stack leads to empty entry */
                if (!parsingTable.get(TOS.getName()).containsKey(inputTokens.get(inputTokenIndex))) {
                    logEntry = new Pair(stackContent.toString(), new Pair<>(inputContents.toString(),
                            "Empty entry action: Skip this token \'" + inputTokens.get(inputTokenIndex) + "\'"));
                    inputTokenIndex++;
                    stack.push(TOS);
                }
                /** if top of stack leads to epsilon */
                else if (parsingTable.get(TOS.getName()).get(inputTokens.get(inputTokenIndex)).get(0).get(0).equals(Constant.EPSILON)) {
                    logEntry = new Pair(stackContent.toString(), new Pair<>(inputContents.toString(),
                            "Epsilon action: Pop stack \'" + TOS.getName() + "\'"));
                }
                /** if top of stack is SYNC_TOK */
                else if (parsingTable.get(TOS.getName()).get(inputTokens.get(inputTokenIndex)).get(0).get(0).equals(Constant.SYNC_TOK)) {
                    logEntry = new Pair(stackContent.toString(), new Pair<>(inputContents.toString(),
                            "SYNC action: Pop stack \'" + TOS.getName() + "\'"));
                } else {/** a production rule needs to be pushed to stack */
                    int lengthOfArray = parsingTable.get(TOS.getName()).get(inputTokens.get(inputTokenIndex)).get(0).size();
                    for (int i = lengthOfArray - 1; i >= 0; i--) {
                        ParsingTreeNode newNode = new ParsingTreeNode(parsingTable.get(TOS.getName()).
                                get(inputTokens.get(inputTokenIndex)).get(0).get(i));
                        if (errorFree) {
                            TOS.getChildren().add(newNode);
                        }
                        stack.push(newNode);
                    }
                    logEntry = new Pair(stackContent.toString(),
                            new Pair<>(inputContents.toString(), "Production rule pushed to stack"));
                }
            } else { /** if top of stack is terminal */
                String actionLog;
                /** if input token match top of stack */
                if (TOS.getName().equals(inputTokens.get(inputTokenIndex))) {
                    actionLog = "Match action: Skip this token \'" + inputTokens.get(inputTokenIndex) + "\'";
                    if (errorFree) {
                        /* val of Node TOS = savedLexemes.getKey() */
                    }
                }
                /** if input token doesn't match top of stack */
                else {
                    actionLog = "No match action: Skip this token \'" + inputTokens.get(inputTokenIndex) + "\'";
                    errorFree = false;
                    stack.push(TOS);
                }
                logEntry = new Pair(stackContent.toString(), new Pair<>(inputContents.toString(), actionLog));
                inputTokenIndex++;
            }
            log.add(logEntry);
        }
        System.out.println(parsingTree);
    }



    public ArrayList<Pair<String, Pair<String, String>>> getLog() {
        return log;
    }
}
