package model.lexical_analyzer.dfa;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.util.Pair;
import model.lexical_analyzer.graph.Graph;
import model.lexical_analyzer.graph.Node;
import utilities.Constant;
import utilities.DfaUtility;

public class DFAOptimizer {
	private Graph DFAMinimized;
	private HashMap<String, Pair<Node, String>> finalStates;

	public DFAOptimizer(DFA DFA) {
		finalStates = new HashMap<>();
		minimizeDFA(DFA);
	}

	private void minimizeDFA(DFA DFA) {
		HashMap<Integer, Integer> nodeParents = new HashMap<>(); /** int node -> int parent */
		HashMap<String, Node> DFATransTable = DFA.getDFATransTable();
		HashMap<Integer, ArrayList<Node>> grouping = new HashMap<>();
		ArrayList<Node> nonAcceptingState = new ArrayList<>();
		ArrayList<Node> acceptingState = new ArrayList<>();
		for (String string : DFATransTable.keySet()) {
			Node node = DFATransTable.get(string);
			if (node.isEnd())
				acceptingState.add(node);
			else
				nonAcceptingState.add(node);
		}

		grouping.put(nonAcceptingState.get(0).getCurrentId(), nonAcceptingState);
		while (!acceptingState.isEmpty()) {
			ArrayList<Node> partition = new ArrayList<>();
			for (int i = 1; i < acceptingState.size(); i++) {
				if (acceptingState.get(i).getNodeTypes().equals(acceptingState.get(0).getNodeTypes())) {
					partition.add(acceptingState.remove(i));
					i--;
				}
			}
			partition.add(acceptingState.remove(0));
			initNodeParents(partition, nodeParents);
			grouping.put(partition.get(0).getCurrentId(), partition);
		}

		initNodeParents(nonAcceptingState, nodeParents);
		HashMap<Integer, ArrayList<Node>> newGrouping = grouping;
		do {
			grouping = newGrouping; /** last grouping */
			newGrouping = constructGroupings(grouping, nodeParents); /** new grouping */
		} while (newGrouping.size() != grouping
				.size()); /**
							 * while last groupings not the same as the new groupings continue to minimize
							 */
		linkDFAFinalGroupings(newGrouping, DFA);
	}

	private void linkDFAFinalGroupings(HashMap<Integer, ArrayList<Node>> finalGroupings, DFA DFA) {
		DFAMinimized = new Graph();
		HashMap<Integer, Node> transTable = new HashMap<>();
		for (Integer partitionID : finalGroupings.keySet()) { /** partition ID */
			Node node = new Node();
			node.setNodeTypes(finalGroupings.get(partitionID).get(0).getNodeTypes());
			if (finalGroupings.get(partitionID).get(0).isEnd())
				node.setEnd(true);

			transTable.put(partitionID, node);
		}
		Integer initialNodeGroupId = DfaUtility.findPartitionOfNode(DFA.getDFA().getInitialNode(), finalGroupings);
		Node initialNode = transTable.get(initialNodeGroupId);
		initialNode.setStart(true);
		DFAMinimized.setInitialNode(initialNode); /** setting the initial node in the minimized DFA */
		for (Integer currentID : finalGroupings.keySet()) {
			Node firstNodeOfGroup = finalGroupings.get(currentID).get(0); /** first node of each partition */
			for (String input : firstNodeOfGroup.getMap()
					.keySet()) { /** use it's edges to find what partition does it points to */
				updateFinalStates(input, currentID, finalGroupings, transTable);
				Node nextNode = firstNodeOfGroup.getMap().get(input).get(0);
				Integer groupingsID = DfaUtility.findPartitionOfNode(nextNode, finalGroupings);
				transTable.get(currentID).addEdge(input, transTable.get(groupingsID));
			}
		}
	}

	/**
	 * define final states and its generation on certain inputs
	 */
	private void updateFinalStates(String input, Integer currentID, HashMap<Integer, ArrayList<Node>> finalGroupings,
			HashMap<Integer, Node> transTable) {
		for (Node oldSource : finalGroupings.get(currentID)) {
			// Node oldSource = finalGroupings.get(currentID).get(0);
			Node newSource = transTable.get(DfaUtility.findPartitionOfNode(oldSource, finalGroupings));
			ArrayList<Node> toNodes = oldSource.getMap().get(input);
			for (Node oldDestination : toNodes) {
				Node newDestination = transTable.get(DfaUtility.findPartitionOfNode(oldDestination, finalGroupings));
				String key = Integer.toString(newSource.getCurrentId()) + Constant.SEPARATOR + input;
				String type = oldDestination.getNodeTypes();
				finalStates.put(key, new Pair<Node, String>(newDestination, type));
			}
		}
	}

	/**
	 * constructing groupings by building a partition for each node and if it isn't
	 * built before added it to the groupings
	 */
	private HashMap<Integer, ArrayList<Node>> constructGroupings(HashMap<Integer, ArrayList<Node>> groupings,
			HashMap<Integer, Integer> nodeParents) {
		HashMap<Integer, Integer> newNodeParents = new HashMap<>();
		HashMap<Integer, ArrayList<Node>> newGroupings = new HashMap<>();
		for (Integer oldGroupID : groupings.keySet()) {
			for (Node node : groupings.get(oldGroupID)) {
				boolean groupMatch = false;
				for (Integer newGroupID : newGroupings.keySet()) {
					Node newGroupingParent = newGroupings.get(newGroupID).get(0);
					groupMatch = DfaUtility.canFit(node, newGroupingParent, nodeParents);
					if (groupMatch) {
						newGroupings.get(newGroupID).add(node);
						newNodeParents.put(node.getCurrentId(), newGroupID);
						break;
					}
				}
				if (!groupMatch) {
					ArrayList<Node> newPartition = new ArrayList<>();
					newPartition.add(node);
					newNodeParents.put(node.getCurrentId(), node.getCurrentId());
					newGroupings.put(node.getCurrentId(), newPartition);
				}
			}
		}
		nodeParents.putAll(newNodeParents);
		return newGroupings;
	}

	public HashMap<String, Pair<Node, String>> getFinalStates() {
		return finalStates;
	}

	public Graph getDFAMinimized() {
		return DFAMinimized;
	}

	private void initNodeParents(ArrayList<Node> partition, HashMap<Integer, Integer> nodeParent) {
		for (Node node : partition) {
			nodeParent.put(node.getCurrentId(), partition.get(0).getCurrentId());
		}
	}
}
