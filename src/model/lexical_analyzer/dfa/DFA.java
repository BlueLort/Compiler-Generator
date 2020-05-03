package model.lexical_analyzer.dfa;

import model.lexical_analyzer.graph.*;
import utilities.Constant;
import utilities.DfaUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DFA {
	private Graph DFA;
	private Stack<ArrayList<Node>> DFAStatesUnmarked;
	private HashMap<String, Node> DFATransTable;

	public DFA(Graph NFACombined) {
		DFA = new Graph();
		DFAStatesUnmarked = new Stack<>();
		DFATransTable = new HashMap<>();
		ArrayList<Node> s0 = new ArrayList<>();
		s0.add(NFACombined.getInitialNode());
		ArrayList<Node> epsClosureS0 = epsilonClosure(s0);
		DFAStatesUnmarked.push(epsClosureS0);
		DFATransTable.put(DfaUtility.createUnionID(epsClosureS0), DFA.getInitialNode());
		constructDFA(NFACombined);
	}

	private void constructDFA(Graph NFACombined) {
		while (!DFAStatesUnmarked.empty()) { /** while there is unmarked states */
			ArrayList<Node> T = DFAStatesUnmarked.pop(); /** mark */
			String TsID = DfaUtility.createUnionID(T);// ID,ID,ID
			ArrayList<Node> U;
			for (String a : DfaUtility.getUnionInputs(T)) { /** for all possible inputs a */
				U = epsilonClosure(move(T, a)); /** U (a new DFA state) = */
				String newNodeTypes = DfaUtility.getNodeType(U);
				String newID = DfaUtility.createUnionID(U);
				if (!DFATransTable.containsKey(newID)) { /** if U is new add to Unmarked and transition table */
					DFAStatesUnmarked.push(U);
					Node node = new Node();
					node.setNodeTypes(newNodeTypes);
					DFATransTable.put(newID, node);
					if (U.contains(NFACombined.getDestination()))
						node.setEnd(true);
				}
				DFATransTable.get(TsID).addEdge(a, DFATransTable.get(newID)); /** transition table [T , a] = U */
			}
		}
	}

	/**
	 * get union of T & all nodes reachable from the set of nodes in T through an
	 * edge epsilon
	 */
	private ArrayList<Node> epsilonClosure(ArrayList<Node> T) {
		Stack<Node> stack = new Stack<>();
		ArrayList<Node> epsilonClosureOut = new ArrayList<>();
		epsilonClosureOut = T;
		/** push all states of T onto stack */
		for (Node node : epsilonClosureOut) {
			stack.push(node);
		}
		/** while stack is not empty pop first element t */
		while (!stack.empty()) {
			Node t = stack.pop();
			HashMap<String, ArrayList<Node>> neighbours = new HashMap<>();
			neighbours = t.getMap();
			/**
			 * search for all unvisited (not in epsilonClosure) nodes reachable from t
			 * through an epsilon edge
			 */
			for (String key : neighbours.keySet()) {
				if (key.equals(Constant.EPSILON)) {
					for (Node node : neighbours.get(key)) {
						if (!epsilonClosureOut.contains(
								node)) { /** if not already in epsilonClosureOut add it and push it to the stack */
							epsilonClosureOut.add(node);
							stack.push(node);
						}
					}
				}
			}
		}
		return epsilonClosureOut;
	}

	/**
	 * Set of NFA states to which there is a transition on input symbol a from some
	 * state s in T.
	 */
	public static ArrayList<Node> move(ArrayList<Node> T, String a) {
		ArrayList<Node> res = new ArrayList<>();
		for (Node node : T) {
			if (node.getMap().keySet().contains(a)) {
				for (Node nodeIterator : node.getMap().get(a)) {
					if (!res.contains(nodeIterator))
						res.add(nodeIterator);
				}
			}
		}
		return res;
	}

	public Graph getDFA() {
		return DFA;
	}

	public HashMap<String, Node> getDFATransTable() {
		return DFATransTable;
	}
}
