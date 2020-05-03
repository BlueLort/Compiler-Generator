package model.parser.parser;

import javafx.util.Pair;
import model.parser.cfg.CFG;
import utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Parser {
    private HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable;
    /**
     * key of outer hashmap is a non terminal entry key of inner hashmap is terminal
     * char input ArrayList value of inner hashmap is set of rules (in case of
     * ambiguous) inner ArrayList is the production
     */

    private CFG grammar;

    private ArrayList<String> inputTokens;


    private ArrayList<Pair<String, Pair<String, String>>> log;

    /**
     * <stack contents< remaining inputs , action >>
     */

    public Parser(ParserGenerator parserGenerator, ArrayList<Pair<String, String>> inputTokens) {
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
        Stack<String> stack = new Stack<>();
        stack.push(Constant.END_MARKER);
        stack.push(grammar.getStartingNonTerminal());
        while (!stack.empty() && inputTokenIndex != inputTokens.size()) {

            Pair<String, Pair<String, String>> logEntry;

            StringBuilder stackContent = new StringBuilder();
            for (String string : stack) {
                stackContent.append(string + "\t\t");
            }

            StringBuilder inputContents = new StringBuilder();
            for (int j = inputTokenIndex; j < inputTokens.size(); j++) {
                inputContents.append(inputTokens.get(j) + "\t\t");
            }


            String TOS = stack.pop();

            /** TODO put strings in constant strings */

            if (grammar.isNonTerminal(TOS)) { /** if top of stack is non terminal */
                /** if top of stack leads to empty entry */
                if (!parsingTable.get(TOS).containsKey(inputTokens.get(inputTokenIndex))) {
                    logEntry = new Pair(stackContent, new Pair<>(inputContents,
                            "event: empty entry action: skip this token (" + inputTokens.get(inputTokenIndex) + ")"));
                    inputTokenIndex++;
                    stack.push(TOS);
                }
                /** if top of stack leads to epsilon */
                else if (parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).get(0).equals(Constant.EPSILON)) {
                    logEntry = new Pair(stackContent, new Pair<>(inputContents,
                            "event: epsilon action: pop stack (" + TOS + ")"));
                }
                /** if top of stack is SYNC_TOK */
                else if (parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).get(0).equals(Constant.SYNC_TOK)) {
                    logEntry = new Pair(stackContent, new Pair<>(inputContents,
                            "event: SYNC action: pop stack (" + TOS + ")"));
                } else {/** a production rule needs to be pushed to stack */
                    int lengthOfArray = parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).size();
                    for (int i = lengthOfArray - 1; i >= 0; i--) {
                        stack.push(parsingTable.get(TOS).get(inputTokens.get(inputTokenIndex)).get(0).get(i));
                    }
                    logEntry = new Pair(stackContent, new Pair<>(inputContents,
                            "event: production rule pushed to stack"));
                }
            } else { /** if top of stack is terminal */
                String actionLog ;
                /** if input token match top of stack */
                if (TOS.equals(inputTokens.get(inputTokenIndex))) {
                    actionLog = "event: match action: skip this token (" + inputTokens.get(inputTokenIndex) + ")";
                }
                /** if input token doesn't match top of stack */
                else {
                    actionLog = "event: no match action: skip this token (" + inputTokens.get(inputTokenIndex) + ")";
                }
                logEntry = new Pair(stackContent, new Pair<>(inputContents, actionLog));
                inputTokenIndex++;
            }
            log.add(logEntry);
        }
    }

    public ArrayList<Pair<String, Pair<String, String>>> getLog() {
        return log;
    }
}
