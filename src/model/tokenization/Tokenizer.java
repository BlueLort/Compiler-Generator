package model.tokenization;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.graph.Node;
import utilities.Constant;

public class Tokenizer {
    private final String SYMBOL_ERROR = "\0";
    private Graph minimalDFA;
    private ArrayList<Pair<String, String>> savedLexems;// first field for the input , 2nd field for the lexeme
    private HashMap<String, Pair<Node, String>> transitionTable; // currentNode,input -> nextNode,Output
    private ArrayList<String> regularExpressions;
    private boolean validTokenization;

    public Tokenizer(DFAOptimizer OptimizedDFA, ArrayList<String> regularExpressionsKeys) {
        this.minimalDFA = OptimizedDFA.getDFAMinimized();
        this.transitionTable = OptimizedDFA.getFinalStates();
        this.regularExpressions = regularExpressionsKeys;
        this.validTokenization = true;
    }

    public ArrayList<Pair<String, String>> getTokens(String input) {
        savedLexems = new ArrayList<>();
        Node start = minimalDFA.getInitialNode();
        int idx = 0;
        int retValue;
        this.validTokenization = true;
        do {
            retValue = addGenerations(input, idx, idx, savedLexems, start);
            if (retValue == -1) {
                idx = getUknownSymbol(input, idx, savedLexems);
                this.validTokenization = false;
                continue;
            }


            if (retValue == -2) {
                idx++;
                savedLexems.add(new Pair<>("", ""));//separator to easily trim error symbols deleted later.
            } else {
                idx = retValue + 1;
            }

        } while (idx < input.length());
        sanitizeLexems(savedLexems);
        return savedLexems;
    }

    private int addGenerations(String input, int startIdx, int idx, ArrayList<Pair<String, String>> lexems, Node currNode) {
        if (idx >= input.length())
            return -2;
        char currentChar = input.charAt(idx);
        if (currentChar == ' ' || currentChar == '\n' || currentChar == '\r' || currentChar == '\t')
            return -2;
        String transition = Integer.toString(currNode.getCurrentId()) + Constant.SEPARATOR + input.charAt(idx);
        Pair<Node, String> nextTransition = transitionTable.get(transition);
        if (nextTransition != null) {
            String acceptanceStates[] = nextTransition.getValue().split(Constant.SEPARATOR);
            String acceptance = getAcceptanceState(acceptanceStates, input.substring(startIdx, idx + 1));
            int retValue = addGenerations(input, startIdx, idx + 1, lexems, nextTransition.getKey());
            if (retValue == -1 || retValue == -2) {
                if (acceptance.equals(""))
                    return -1;
                lexems.add(new Pair<>(input.substring(startIdx, idx + 1), acceptance));
                return idx;
            } else {
                return retValue;
            }
        }
        return -1;
    }

    private String getAcceptanceState(String acceptanceStates[], String input) {
        if (acceptanceStates.length == 1)
            return acceptanceStates[0];
        for (String s : acceptanceStates) {
            if (input.equals(s))
                return input;
        }
        for (String reg : regularExpressions) {
            for (String s : acceptanceStates) {
                if (reg.equals(s))
                    return reg;
            }
        }
        return input;
    }

    //returns index to the start of the new valid inputs
    private int getUknownSymbol(String input, int startIdx, ArrayList<Pair<String, String>> lexems) {
        String appendedMatches = removeIncorrectMatches(lexems);
        int idx = startIdx;
        int retValue;
        Node startNode = minimalDFA.getInitialNode();
        do {
            retValue = addGenerations(input, idx, idx, lexems, startNode);
            if (retValue == -1) {
                appendedMatches += input.charAt(idx);
                idx++;
            } else if (retValue != -2) {
                if (isRegex(lexems.get(lexems.size() - 1).getValue())) {
                    appendedMatches += lexems.get(lexems.size() - 1).getKey();
                    idx = retValue + 1;
                } else {// keyword or operator found so just pop it and break [ works like a separator ]
                    lexems.remove(lexems.size() - 1);
                    break;
                }
            }
        } while (retValue != -2);
        removeIncorrectMatches(lexems);
        lexems.add(new Pair<>(appendedMatches, SYMBOL_ERROR));

        return idx;
    }

    private String removeIncorrectMatches(ArrayList<Pair<String, String>> lexems) {
        String appendedMatches = "";
        while (lexems.size() > 0) {
            Pair<String, String> lastMatch = lexems.get(lexems.size() - 1);
            if (isRegex(lastMatch.getValue()) != true) {
                break;
            }
            appendedMatches += lastMatch.getKey();
            lexems.remove(lexems.size() - 1);
        }
        return appendedMatches;
    }

    private boolean isRegex(String match) {
        for (String reg : regularExpressions) {
            if (reg.equals(match)) return true;
        }
        return false;
    }

    void sanitizeLexems(ArrayList<Pair<String, String>> lexems) {
        int idx = 0;
        int size = lexems.size();
        while (idx < size) {
            if (lexems.get(idx).getKey().equals("")) {
                lexems.remove(idx);
                size--;
            } else {
                idx++;
            }
        }
    }

    public ArrayList<Pair<String, String>> getSavedLexems() {
        return savedLexems;
    }

    public HashMap<String, Pair<Node, String>> getTransitionTable() {
        return transitionTable;
    }

    public boolean isValidTokenization() {
        return validTokenization;
    }
}
