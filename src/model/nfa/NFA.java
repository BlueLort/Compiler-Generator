package model.nfa;

import java.util.ArrayList;
import java.util.Map.Entry;

import model.graph.Graph;
import utilities.GraphUtility;

public class NFA {
	
	private ArrayList<Graph> combinedNfa;
	private Graph combinedGraph;
	public NFA(RegularDefinition regularDefinition ,Keyword keyword, Punctuation punctuation, RegularExpression regex) {
		combinedNfa = new ArrayList<Graph>();
		combine(keyword,punctuation,regex);
	}

	private void addToList(Keyword keyword, Punctuation punctuation, RegularExpression regex) {
		for (Entry<String, Graph> entry : punctuation.getPunctuationNfa().entrySet()) {
			combinedNfa.add(entry.getValue());
		}
		for (Entry<String, Graph> entry : keyword.getKeywordNfa().entrySet()) {
			combinedNfa.add(entry.getValue());
		}
		for (Entry<String, Graph> entry : regex.getRegExpressionNfa().entrySet()) {
			combinedNfa.add(entry.getValue());
		}
	}

	public Graph getCombinedGraph() {
		return combinedGraph;
	}

	private void combine(Keyword keyword, Punctuation punctuation, RegularExpression regex) {
		addToList(keyword,punctuation,regex);
		combinedGraph = GraphUtility.or(this.combinedNfa);
	}
}
