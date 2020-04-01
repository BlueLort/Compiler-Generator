package model.tokenization;

import javafx.util.Pair;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Tokenizer {
    private Graph minimalDFA;
    private ArrayList<String> savedLexems;
    private ArrayList<String> savedMatches;
    private HashMap<String, Pair<Node, String>> transitionTable; //currentNode,input -> nextNode,Output

    public Tokenizer(DFAOptimizer OptimizedDFA) {
        this.minimalDFA = OptimizedDFA.getDFAMinimized();
        HashMap<Pair<Pair<Node, Node>, String>, String> finalStates = OptimizedDFA.getFinalStates();
        transitionTable = new HashMap<String, Pair<Node, String>>();
        constructTransitionTable(finalStates);
    }

    private void constructTransitionTable(HashMap<Pair<Pair<Node, Node>, String>, String> finalStates) {
        for (Map.Entry<Pair<Pair<Node, Node>, String>, String> entry : finalStates.entrySet()) {
            String key = Integer.toString(entry.getKey().getKey().getKey().getCurrentId()) + "," + entry.getKey().getValue();
            Pair<Node, String> val = new Pair(entry.getKey().getKey().getValue(), entry.getValue());
            transitionTable.put(key, val);
        }
        System.out.println(transitionTable.get("78,;"));
        System.out.println(transitionTable.get("78,i"));
    }

    public ArrayList<String> getTokens(String input) {
        savedLexems = new ArrayList<>();
        savedMatches = new ArrayList<>();
        Node start = minimalDFA.getInitialNode();
        int idx = 0;
        int retValue;
        do {
            retValue = addGenerations(input, idx, savedLexems, start);
            if (retValue == -1) return null;

            if (retValue == -2) idx++;
            else {
                savedMatches.add(input.substring(idx, retValue + 1));
                idx = retValue + 1;
            }
        } while (idx < input.length());
        //TODO CREATE INFO WINDOW
        return savedLexems;
    }

    private int addGenerations(String input, int idx, ArrayList<String> lexems, Node currNode) {
        char currentChar = input.charAt(idx);
        if (currentChar == ' ' || currentChar == '\n' || idx >= input.length()) return -2;
        String transition = Integer.toString(currNode.getCurrentId()) + "," + input.charAt(idx);
        Pair<Node, String> nextTransition = transitionTable.get(transition);
        if (nextTransition != null) {
            String acceptance = nextTransition.getValue();
            int retValue = addGenerations(input, idx + 1, lexems, nextTransition.getKey());
            if (retValue == -1 || retValue == -2) {
                if (acceptance.equals("")) return -1;
                lexems.add(acceptance);
                return idx;
            } else {
                return retValue;
            }
        }
        return -1;
    }


}
