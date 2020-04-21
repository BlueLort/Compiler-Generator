package model.parser.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import model.parser.cfg.CFG;
import utilities.Constant;

public class Parser {
	
	/*
	 * NON RECURSIVE PREDICTIVE PARSER
	 * FIRST, FOLLOW, BUILD PARSING TABLE
	 */

	private CFG grammar;
	private HashMap<String, HashSet<String>> first;
	private HashMap<String, HashSet<String>> follow;
	private ArrayList<String> nonTerminals;

	public Parser(CFG grammar) {
		this.grammar = grammar;
		this.first = new HashMap<String, HashSet<String>>();
		this.follow = new HashMap<String, HashSet<String>>();
	}

	public void constructParser() {
		init();
		first();
		follow();
	}

	private void first() {
		ArrayList<String> visited = new ArrayList<String>();

		for (int i = 0; i < nonTerminals.size(); i++) {
			String nonTerminal = nonTerminals.get(i);
			if (!visited.contains(nonTerminal)) {
				visited.add(nonTerminal);
				firstRecursive(nonTerminal, visited);
			}
		}
		/*
		 * Iterator<Entry<String, HashSet<String>>> iterator =
		 * first.entrySet().iterator(); while (iterator.hasNext()) { Entry<String,
		 * HashSet<String>> entry = iterator.next(); if
		 * (!nonTerminals.contains(entry.getKey())) iterator.remove(); }
		 * 
		 * for (Entry<String, HashSet<String>> entry : first.entrySet()) {
		 * System.out.print("First Of " + entry.getKey() + " : "); for (String s :
		 * entry.getValue()) System.out.print(s + " "); System.out.println(); }
		 */
	}

	private void firstRecursive(String start, ArrayList<String> visited) {

		/*
		 * If x is a terminal, then FIRST(x) = { ‘x’ } If x-> Є, is a production rule,
		 * then add Є to FIRST(x). 
		 * If X->Y1 Y2 Y3….Yn is a production, FIRST(X) = FIRST(Y1).
		 * If FIRST(Y1) contains Є then FIRST(X) = { FIRST(Y1) – Є } U {FIRST(Y2) }.
		 * If FIRST (Yi) contains Є for all i = 1 to n, then add Є to FIRST(X).
		 */
		if (!grammar.isNonTerminal(start) || start.equals(Constant.EPSILON)) {
			first.put(start, new HashSet<String>());
			first.get(start).add(start);
			return;
		}

		// Get the right hand side of the current non terminal
		ArrayList<ArrayList<String>> rhs = grammar.getRHS(start);
		// Loop over the rhs
		for (ArrayList<String> temp : rhs) {
			// Get the first word
			String firstWord = temp.get(0);
			if (!visited.contains(firstWord)) {
				visited.add(firstWord);
				// Make recursive call
				firstRecursive(firstWord, visited);
			}
			// Get the first of the child
			HashSet<String> firstOfChild = first.get(firstWord);
			// Add it to the parent's hashset
			for (String s : firstOfChild) {
				first.get(start).add(s);
			}

		}

	}
	
	private void follow () {
		
	}

	private void init() {
		nonTerminals = grammar.getNonTerminals();
		for (String nonTerminal : nonTerminals) {
			first.put(nonTerminal, new HashSet<String>());
			follow.put(nonTerminal, new HashSet<String>());
		}
	}

}
