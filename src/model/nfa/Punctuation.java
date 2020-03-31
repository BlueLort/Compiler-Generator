package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.NfaUtility;

public class Punctuation {

	private HashMap<String, Graph> punctuationNfa;

	public Punctuation(RulesContainer rulesCont) {
		punctuationNfa = new HashMap<String, Graph>();
		punctuationToNfa(rulesCont);
	}

	private void punctuationToNfa(RulesContainer rulesContainer) {

		for (int i = 0; i < rulesContainer.getOperators().size(); i++) {
			String operator = rulesContainer.getOperator(i);
			String[] operatorCharacters = operator.replace("\\", "").split("");
			ArrayList<String> characters = NfaUtility.addConcatSymbolToWords(operatorCharacters);
			ArrayList<String> postFixExpression = NfaUtility.infixToPostFix(characters);
			Graph nfa = createNfa(postFixExpression);
			punctuationNfa.put(operator, nfa);
			nfa.getDestination().setNodeType(operator);
		}
	}

	private static Graph createNfa(ArrayList<String> expression) {
		// create a stack
		Stack<Graph> nfa = new Stack<Graph>();

		// Scan all characters one by one
		for (int i = 0; i < expression.size(); i++) {
			String currentExpression = expression.get(i);
			nfa.push(new Graph(currentExpression));
		}
		return nfa.pop();
	}

	public HashMap<String, Graph> getPunctuationNfa() {
		return punctuationNfa;
	}

}
