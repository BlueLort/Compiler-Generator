package model.parser.parser;

import javafx.util.Pair;
import model.parser.cfg.CFG;
import utilities.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Parser {
    private HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> parsingTable;
    /** key of outer hashmap is a non terminal entry
    *   key of inner hashmap is terminal char input
    *   ArrayList value of inner hashmap is set of rules (in case of ambiguous)
    *   inner ArrayList is the production */

    private CFG grammar;

    private ArrayList<Pair<String, String>>  inputTokens;
    private HashMap<Integer, String>  errors;
    public Parser(  ParserGenerator parserGenerator,
                    ArrayList<Pair<String, String>> inputTokens) {
        this.parsingTable = parserGenerator.getParsingTable();
        this.inputTokens = inputTokens;
        this.grammar = parserGenerator.getGrammar();
    }

    public void parse() {
        int inputTokenIndex = 0;
        Stack<String>  stack = new Stack<>();
        stack.push(Constant.END_MARKER);
        stack.push(grammar.getStartingNonTerminal());
        while (!stack.empty() && inputTokenIndex != inputTokens.size()){
            String TOS = stack.pop();
            if (grammar.isNonTerminal(TOS)){ /** if top of stack is non terminal */
            
            } else {    /** if top of stack is terminal */

            }
        }
    }
}
