package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map.Entry;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.Constant;
import utilities.GraphUtility;
import utilities.NfaUtility;

public class Keyword {
	private HashMap<String, Graph> keywordNfa;

	private RulesContainer rulesContainer;

	public Keyword(RulesContainer rulesCont) {
		keywordNfa = new HashMap<String, Graph>();
		this.rulesContainer = rulesCont;
	}

	public void keywordToNfa() {

		for (int i = 0; i < rulesContainer.getKeywords().size(); i++) {
			String keyword = rulesContainer.getKeyword(i);
			String[] keywordCharacters = keyword.split("");
			ArrayList<String> characters = NfaUtility.addConcatSymbolToWords(keywordCharacters);
			ArrayList<String> postFixExpression = NfaUtility.infixToPostFix(characters);
			Graph nfa = createNfa(postFixExpression);
			keywordNfa.put(keyword, nfa);

		}
	}

	public void DFSGraphs() {
		for (Entry<String, Graph> entry : keywordNfa.entrySet()) {
			// entry.getValue().DFS();
			System.out.println("\n\n\n");
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
	}

	private static Graph createNfa(ArrayList<String> expression) {
		// create a stack
		Stack<Graph> nfa = new Stack<Graph>();

		// Scan all characters one by one
		for (int i = 0; i < expression.size(); i++) {
			String currentExpression = expression.get(i);

			if (currentExpression.equals(Constant.CONCATENATE)) {
				Graph right = nfa.pop();
				Graph left = nfa.pop();
				nfa.push(GraphUtility.concatenate(left, right));
			} else {
				nfa.push(new Graph(currentExpression));
			}
		}
		return nfa.pop();
	}

	public HashMap<String, Graph> getKeywordNfa() {
		return keywordNfa;
	}

}
