package model.lexical_analyzer.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import model.lexical_analyzer.construction.LexicalRulesContainer;
import model.lexical_analyzer.graph.Graph;
import utilities.NfaUtility;

public class Punctuation {

	private HashMap<String, Graph> punctuationNfa;

	public Punctuation(LexicalRulesContainer rulesCont) {
		punctuationNfa = new HashMap<String, Graph>();
		punctuationToNfa(rulesCont);
	}

	private void punctuationToNfa(LexicalRulesContainer lexicalRulesContainer) {

		for (int i = 0; i < lexicalRulesContainer.getOperators().size(); i++) {
			String operator = lexicalRulesContainer.getOperator(i);
			operator = operator.replace("\\", "");
			String[] operatorCharacters = operator.split("");
			ArrayList<String> characters = NfaUtility.addConcatSymbolToWords(operatorCharacters);
			ArrayList<String> postFixExpression = NfaUtility.infixToPostFix(characters);
			Graph nfa = createNfa(postFixExpression);
			punctuationNfa.put(operator, nfa);
			nfa.getDestination().setNodeTypes(operator);
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
