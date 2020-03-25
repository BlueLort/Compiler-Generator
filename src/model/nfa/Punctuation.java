package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.NfaUtility;

public class Punctuation {

	HashMap<String, Graph> punctuationNfa;

	RulesContainer rulesContainer;

	public Punctuation(RulesContainer rulesCont) {
		punctuationNfa = new HashMap<String, Graph>();
		this.rulesContainer = rulesCont;
	}

	public void punctuationToNfa() {

		for (int i = 0; i < rulesContainer.getOperators().size(); i++) {
			String operator = rulesContainer.GetOperator(i);
			String[] operatorCharacters = operator.replace("\\", "").split("");
			ArrayList<String> characters = NfaUtility.addConcatSymbolToWords(operatorCharacters);
			String postFixExpression = NfaUtility.infixToPostFix(characters);
			Graph nfa = createNfa(postFixExpression);
			punctuationNfa.put(operator, nfa);
		}
	}

	public void dfsGraphs() {
		for (Entry<String, Graph> entry : punctuationNfa.entrySet()) {
			System.out.print(entry.getKey() + " ");
			entry.getValue().dfs();
			System.out.println();
		}
	}
	
	public static Graph createNfa(String expression) {
		// create a stack
		Stack<Graph> nfa = new Stack<Graph>();

		// Scan all characters one by one
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);
			String nodeName = String.valueOf(c);
			nfa.push(new Graph(nodeName));
		}
		return nfa.pop();
	}


}
