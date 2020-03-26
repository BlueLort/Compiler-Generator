package model.nfa;

import java.util.ArrayList;
import java.util.Map.Entry;

import model.graph.Graph;
import utilities.GraphUtility;

public class NFA {
	
	private ArrayList<Graph> combinedNfa;
	private RegularExpression regex;
	private Punctuation punctuation;
	private Keyword keyword;
	RegularDefinition regularDefinition;

	public NFA(RegularDefinition regularDefinition ,Keyword keyword, Punctuation punctuation, RegularExpression regex) {
		this.regularDefinition = regularDefinition;
		this.keyword = keyword;
		this.punctuation = punctuation;
		this.regex = regex;
		combinedNfa = new ArrayList<Graph>();
	}
	
	private void getNfa() {
		regularDefinition.definitionsToNfa();
		// regularDefinition.DFSGraphs();
		keyword.keywordToNfa();
		// keyword.DFSGraphs();
		punctuation.punctuationToNfa();
		// punctuation.DFSGraphs();
		regex.regexToNfa();
		//regex.DFSGraphs();
	}
	
	private void addToList() {
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
	
	
	public Graph combine() {
		getNfa();
		addToList();
		Graph combinedNfas = GraphUtility.or(this.combinedNfa);
		return combinedNfas;
	}
}
