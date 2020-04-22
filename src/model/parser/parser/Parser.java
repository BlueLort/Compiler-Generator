package model.parser.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

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
		printFirstAndFollow();
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
		 */

		return;
	}

	private void firstRecursive(String start, ArrayList<String> visited) {

		/*
		 * If x is a terminal, then FIRST(x) = { ‘x’ } 
		 * If x-> Є, is a production rule, then add Є to FIRST(x). 
		 * If X->Y1 Y2 Y3….Yn is a production, FIRST(X) = FIRST(Y1). 
		 * If FIRST(Y1) contains Є then FIRST(X) = { FIRST(Y1) – Є } U { FIRST(Y2) }. 
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
			for (int j = 0; j < temp.size(); j++) {
				String word = temp.get(j);
				if (!visited.contains(word)) {
					visited.add(word);
					// Make recursive call
					firstRecursive(word, visited);
				}
				// Get the first of the child
				HashSet<String> firstOfChild = first.get(word);
				// Add it to the parent's hashset
				for (String s : firstOfChild) {
					first.get(start).add(s);
				}
				// If the first of the word contains epsilon, then stop
				if (!first.get(word).contains(Constant.EPSILON)) {
					first.get(start).remove(Constant.EPSILON);
					break;
				}

			}

		}

	}

	private void follow() {

		/*
		 * 1) FOLLOW(S) = { $ } // where S is the starting Non-Terminal 
		 * 2) If A -> pBq is a production, where p, B and q are any grammar symbols, 
		 * then everything in FIRST(q) except Є is in FOLLOW(B). 
		 * 3) If A->pB is a production, then everything in FOLLOW(A) is in FOLLOW(B). 
		 * 4) If A->pBq is a production and FIRST(q) contains Є, then FOLLOW(B) contains { FIRST(q) – Є } U FOLLOW(A)
		 */

		HashMap<String, ArrayList<String>> followDependency = new HashMap<String, ArrayList<String>>();
		for (int i = 0; i < nonTerminals.size(); i++) {
			followDependency.put(nonTerminals.get(i), new ArrayList<>());
		}

		// First rule : Add to the follow Of starting non terminal = $
		follow.get(grammar.getStartingNonTerminal()).add(Constant.END_MARKER);

		// Loop over all the non terminals
		for (int i = 0; i < nonTerminals.size(); i++) {
			String nonTerminal = nonTerminals.get(i);
			// Get the RHS
			ArrayList<ArrayList<String>> rhs = grammar.getRHS(nonTerminal);
			for (ArrayList<String> temp : rhs) {
				for (int j = 0; j < temp.size(); j++) {
					// GET current symbol
					String current = temp.get(j);
					// If its a non terminal
					if (grammar.isNonTerminal(current)) {
						/*
						 * If J == last Index then the follow of the current depends on the follow of
						 * the original non terminal
						 */
						if (j == temp.size() - 1)
							followDependency.get(current).add(nonTerminal);
						else {
							// Loop over the rest of the rhs
							for (int k = j + 1; k < temp.size(); k++) {
								String following = temp.get(k);
								// If the next is a non terminal
								if (grammar.isNonTerminal(following)) {
									/*
									 * If k == Last index and there's an epsilon in its start Then the follow of the
									 * current depends on the follow of the original non terminal
									 */
									if (k == temp.size() - 1 && first.get(following).contains(Constant.EPSILON))
										followDependency.get(current).add(nonTerminal);
									// Follow of current = first of next except epsilon
									for (String first : first.get(following)) {
										this.follow.get(current).add(first);
									}
									// If you encounter an epsilon in its start then continue looping
									if (first.get(following).contains(Constant.EPSILON))
										follow.get(current).remove(Constant.EPSILON);
									// Else break the loop and DONE
									else
										break;
								} else
									this.follow.get(current).add(following);
							}
						}
					}
				}
			}
		}
		// Loop until no updates are done
		while (true) {
			boolean updates = false;
			// Loop over all the non terminals
			for (Entry<String, ArrayList<String>> entry : followDependency.entrySet()) {
				// Get the dependency of each non terminal
				String nonTerminal = entry.getKey();
				ArrayList<String> dependency = entry.getValue();
				// Save old follow of the current non terminal
				HashSet<String> oldFollow = follow.get(nonTerminal);
				// Loop over the current non terminals dependency and updates the follow
				for (int i = 0; i < dependency.size(); i++) {
					for (String s : follow.get(dependency.get(i)))
						follow.get(nonTerminal).add(s);
					// If the old follow is not the same as the updated follow, set the flag
					if (!compareHashSets(oldFollow, follow.get(nonTerminal)))
						updates = true;
				}
			}
			// If no more updates are done, terminate loop
			if (!updates)
				break;
		}

	}

	private void init() {
		nonTerminals = grammar.getNonTerminals();
		for (String nonTerminal : nonTerminals) {
			first.put(nonTerminal, new HashSet<String>());
			follow.put(nonTerminal, new HashSet<String>());
		}
		return;
	}

	private boolean compareHashSets(HashSet<String> firstSet, HashSet<String> secondSet) {
		if (firstSet.size() != secondSet.size()) {
			return false;
		}
		return secondSet.containsAll(firstSet);
	}

	private void printFirstAndFollow() {
		for (Entry<String, HashSet<String>> entry : first.entrySet()) {
			System.out.print("First Of " + entry.getKey() + " : ");
			for (String s : entry.getValue())
				System.out.print(s + " ");
			System.out.println();
		}

		for (Entry<String, HashSet<String>> entry : follow.entrySet()) {
			System.out.print("Follow Of " + entry.getKey() + " : ");
			for (String s : entry.getValue())
				System.out.print(s + " ");
			System.out.println();
		}

		return;
	}

}