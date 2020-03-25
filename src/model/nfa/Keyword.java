package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.NfaUtility;

public class Keyword {
	HashMap<String, Graph> keywordNfa;

	RulesContainer rulesContainer;

	public Keyword(RulesContainer rulesCont) {
		keywordNfa = new HashMap<String, Graph>();
		this.rulesContainer = rulesCont;
	}

	public void keywordToNfa() {

		for (int i = 0; i < rulesContainer.getKeywords().size(); i++) {
			String keyword = rulesContainer.GetKeyword(i);
			String[] keywordCharacters = keyword.split("");
			ArrayList<String> characters = NfaUtility.addConcatSymbolToWords(keywordCharacters);
			String postFixExpression = NfaUtility.infixToPostFix(characters);
			Graph nfa = NfaUtility.createNfa(postFixExpression);
			keywordNfa.put(keyword, nfa);

		}
	}

	public void dfsGraphs() {
		for (Entry<String, Graph> entry : keywordNfa.entrySet()) {
			System.out.print(entry.getKey() + " ");
			entry.getValue().dfs();
			System.out.println();
		}
	}

}
