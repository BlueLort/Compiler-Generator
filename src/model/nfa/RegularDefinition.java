package model.nfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import model.construction.RulesContainer;
import model.graph.Graph;
import utilities.Constant;
import utilities.GraphUtility;

public class RegularDefinition {

	private HashMap<String, Graph> definitionNfa;

	private RulesContainer rulesContainer;

	public RegularDefinition(RulesContainer rulesCont) {
		definitionNfa = new HashMap<String, Graph>();
		this.rulesContainer = rulesCont;
	}

	public void definitionsToNfa() {

		for (int i = 0; i < rulesContainer.getRegularDefinitionsKeys().size(); i++) {
			String definitionKey = rulesContainer.getRegularDefinitionsKeys().get(i);
			String definitionValue = rulesContainer.getRegularDefinition(definitionKey);

			definitionValue = separateRDByOrs(definitionValue);

			Graph currentDefinitionNfa = createNfa(definitionValue);
			definitionNfa.put(definitionKey, currentDefinitionNfa);
		}
	}

	private Graph createNfa(String definition) {

		Stack<Graph> nfa = new Stack<Graph>();
		Stack<Character> operator = new Stack<Character>();
		ArrayList<Graph> merge = new ArrayList<Graph>();

		int i = 0;

		while (i < definition.length()) {
			char c = definition.charAt(i);
			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				int j = i;
				while (j < definition.length()
						&& (Character.isAlphabetic(definition.charAt(j)) || Character.isDigit(definition.charAt(j)))) {
					j++;
				}

				String nodeName = definition.substring(i, j);
				if (nodeName.length() == 1)
					nfa.push(new Graph(nodeName));
				else if (definitionNfa.containsKey(nodeName)) {
					nfa.push(definitionNfa.get(nodeName));
				}
				i = j;
			} else {
				if (c == Constant.PLUS.charAt(0)) {
					// Plus operator
					nfa.push(GraphUtility.plusClosure(nfa.pop()));
				} else if (c == Constant.KLEENE.charAt(0)) {
					// Kleene Closure
					nfa.push(GraphUtility.kleeneClosure(nfa.pop()));
				} else if (c == '(') {
					operator.push(c);
				} else if (c == Constant.OR.charAt(0)) {
					operator.push(c);
				} else if (c == ')') {
					// Pop until you find a ')'
					merge.clear();
					operator.push(c);
					while (operator.pop() != '(') {
						Graph right = nfa.pop();
						Graph left = nfa.pop();
						nfa.push(GraphUtility.or(right, left));
					}
				}
				i++;
			}
		}

		if (nfa.size() == 1)
			return nfa.pop();

		merge.clear();

		while (!nfa.isEmpty()) {
			merge.add(nfa.pop());
		}

		Graph mergedGraph = GraphUtility.or(merge);
		return mergedGraph;
	}

	private String separateRDByOrs(String definition) {

		StringBuilder expression = new StringBuilder();

		boolean range = false;

		char start = Character.MIN_VALUE;

		definition = definition.replace(" ", "");

		for (char c : definition.toCharArray()) {

			if (Character.isAlphabetic(c) || Character.isDigit(c)) {
				if (!range) {
					start = c;
					expression.append(c);
				} else {

					range = false;
					String separated = null;

					if (Constant.ALPHABETS.indexOf(c) != -1) {
						separated = Constant.ALPHABETS;
					} else if ((Constant.ALPHABETS.toUpperCase()).indexOf(c) != -1) {
						separated = Constant.ALPHABETS.toUpperCase();
					} else if (Constant.DIGITS.indexOf(c) != -1) {
						separated = Constant.DIGITS;
					}

					int startIndex = separated.indexOf(start);
					int endIndex = separated.indexOf(c);

					expression.append('|');

					int iterator = startIndex + 1;
					while (iterator < endIndex) {
						expression.append(separated.charAt(iterator));
						expression.append('|');
						iterator++;
					}

					expression.append(separated.charAt(endIndex));

				}
			} else {
				if (!range) {
					if (c == '-')
						range = true;
					else
						expression.append(c);
				}
			}
		}
		return expression.toString();
	}

	public void DFSGraphs() {
		for (Entry<String, Graph> entry : definitionNfa.entrySet()) {
			entry.getValue().DFS();
		}
	}

	public HashMap<String, Graph> getDefinitionNfa() {
		return definitionNfa;
	}

}
