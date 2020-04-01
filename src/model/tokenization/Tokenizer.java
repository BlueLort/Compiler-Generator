package model.tokenization;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.graph.Node;
import utilities.Constant;

public class Tokenizer {
	private Graph minimalDFA;
	private ArrayList<Pair<String, String>> savedLexems;// first field for the input , 2nd field for the lexeme
	private HashMap<String, Pair<Node, String>> transitionTable; // currentNode,input -> nextNode,Output

	public Tokenizer(DFAOptimizer OptimizedDFA) {
		this.minimalDFA = OptimizedDFA.getDFAMinimized();
		this.transitionTable = OptimizedDFA.getFinalStates();
	}

	public ArrayList<Pair<String, String>> getTokens(String input) {
		savedLexems = new ArrayList<>();
		Node start = minimalDFA.getInitialNode();
		int idx = 0;
		int retValue;
		do {
			retValue = addGenerations(input, idx, idx, savedLexems, start);
			if (retValue == -1)
				return null;

			if (retValue == -2)
				idx++;
			else
				idx = retValue + 1;
		} while (idx < input.length());
		// TODO CREATE INFO WINDOW
		return savedLexems;
	}

	private int addGenerations(String input, int startIdx, int idx, ArrayList<Pair<String, String>> lexems,
			Node currNode) {
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
		return input;// TODO MAKE SURE THIS ASSUMPTION CORRECT
	}

	public ArrayList<Pair<String, String>> getSavedLexems() {
		return savedLexems;
	}

	public HashMap<String, Pair<Node, String>> getTransitionTable() {
		return transitionTable;
	}
}
