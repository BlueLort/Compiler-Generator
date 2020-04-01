package model.dfa;

import javafx.util.Pair;
import model.graph.Graph;
import model.graph.Node;
import utilities.DfaUtility;

import java.util.*;

public class DFAOptimizer {
    private Graph DFAMinimized;

    private HashMap<Pair<Pair<Node, Node>, String>, String> finalStates;

    public DFAOptimizer(DFA DFA) {
        finalStates = new HashMap<>();
        minimizeDFA(DFA);
        System.out.println("************" + "\t\t\t minimized DFA" + "\t\t****************");
        System.out.println(DFAMinimized.toString());
        System.out.println("\n\n\n\n\n");
        System.out.println("************" + "\t\t\t original DFA" + "\t\t****************");
        System.out.println(DFA.getDFA().toString());
    }


    private void minimizeDFA(DFA DFA) {
        HashMap<String, Node> DFATransTable = DFA.getDFATransTable();
        HashMap<String, ArrayList<Node>> grouping = new HashMap<>();
        ArrayList<Node> nonAcceptingState = new ArrayList<>();
        ArrayList<Node> acceptingState = new ArrayList<>();
        for (String string : DFATransTable.keySet()) {
            Node node = DFATransTable.get(string);
            if (node.isEnd())
                acceptingState.add(node);
            else nonAcceptingState.add(node);
        }
        Collections.sort(nonAcceptingState);
        Collections.sort(acceptingState);
        grouping.put("start", nonAcceptingState);
        grouping.put("final", acceptingState);
        HashMap<String, ArrayList<Node>> newGrouping = grouping;
        do {
            grouping = newGrouping;                               /** last grouping */
            newGrouping = constructGroupings(grouping);               /** new grouping */
        }
        while (!DfaUtility.isTwoGroupingsEqual(newGrouping, grouping)); /** while last groupings not the same as the new groupings continue to minimize */
        linkDFAFinalGroupings(newGrouping, DFA);
    }


    private void linkDFAFinalGroupings(HashMap<String, ArrayList<Node>> finalGroupings, DFA DFA) {
        DFAMinimized = new Graph();
        HashMap<String, Node> transTable = new HashMap<>();
        for (String string : finalGroupings.keySet()) {
            Node node = new Node();
            if (DfaUtility.isEndGroupings(finalGroupings.get(string)))
                node.setEnd(true);
            transTable.put(string, node);
        }
        String initialNodeGroupId = DfaUtility.findPartitionOfNode(DFA.getDFA().getInitialNode(), finalGroupings);
        Node initialNode = transTable.get(initialNodeGroupId);
        initialNode.setStart(true);
        DFAMinimized.setInitialNode(initialNode); /** setting the initial node in the minimized DFA */
        for (String currentID : finalGroupings.keySet()) {
            Node firstNodeOfGroup = finalGroupings.get(currentID).get(0); /** first node of each partition use it's edges to find what partition does it points to*/
            for (String input : firstNodeOfGroup.getMap().keySet()) {
                updateFinalStates(input, currentID, finalGroupings, transTable);
                Node nextNode = firstNodeOfGroup.getMap().get(input).get(0);
                String groupingsID = DfaUtility.findPartitionOfNode(nextNode, finalGroupings);
                transTable.get(currentID).addEdge(input, transTable.get(groupingsID));
            }
        }
    }

    /**
     * define final states and its generation on certain inputs
     */
    private void updateFinalStates(String input, String currentID, HashMap<String, ArrayList<Node>> finalGroupings,
                                   HashMap<String, Node> transTable) {
        Node oldSource = finalGroupings.get(currentID).get(0);
        ArrayList<Node> toNodes = oldSource.getMap().get(input);
        Node newSource = transTable.get(DfaUtility.findPartitionOfNode(oldSource, finalGroupings));
        for (Node to : toNodes) {
            Node destination = transTable.get(DfaUtility.findPartitionOfNode(to, finalGroupings));
            ArrayList<String> types = to.getNodeTypes();
            StringBuilder type = new StringBuilder("");
            for(String s:types){
                type.append(s);
                type.append(',');
            }

            finalStates.put(new Pair(new Pair(newSource, destination), input), type.toString().substring(0,type.length()>0?type.length()-1:0));
        }
    }
    /**
     * NOTE: Whenever the array list is accessed it must be sorted  to maintain order to compare the lists easily
     */

    /**
     * constructing groupings by building a partition for each node and if it isn't built before added it to the groupings
     */
    private HashMap<String, ArrayList<Node>> constructGroupings(HashMap<String, ArrayList<Node>> groupings) {
        HashMap<String, ArrayList<Node>> newGroupings = new HashMap<>();
        for (String string : groupings.keySet()) {
            for (Node node : groupings.get(string)) {
                String newGroupingsID = DfaUtility.createGroupingsID(node, groupings, string);
                if (newGroupings.keySet().contains(newGroupingsID)) {
                    newGroupings.get(newGroupingsID).add(node);
                    Collections.sort(newGroupings.get(newGroupingsID));
                } else {
                    ArrayList<Node> newPartition = new ArrayList<>();
                    newPartition.add(node);
                    newGroupings.put(newGroupingsID, newPartition);
                }
            }
        }
        return newGroupings;
    }


    public HashMap<Pair<Pair<Node, Node>, String>, String> getFinalStates() {
        return finalStates;
    }

    public Graph getDFAMinimized() {
        return DFAMinimized;
    }
}
