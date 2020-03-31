package model.dfa;

import model.graph.Graph;
import model.graph.Node;
import utilities.DfaUtility;
import java.util.*;

public class DFAOptimizer {
    Graph DFA;
    Graph DFAMinimized;
    HashMap<String, Node> DFATransTable;
    HashMap<String, Node> minimizedDFATransTable;
    HashMap<String, ArrayList<Node>> finalGroupings;

    public DFAOptimizer(DFA DFA) {
        this.DFA = DFA.getDFA();
        this.DFATransTable = DFA.getDFATransTable();
        minimizeDFA();
        linkDFAFinalGroupings();
        System.out.println("************"+"\t\t\t minimized DFA" + "\t\t****************");
        System.out.println(DFAMinimized.toString());
        System.out.println("\n\n\n\n\n");
        System.out.println("************"+"\t\t\t original DFA" + "\t\t****************");
        System.out.println(DFA.getDFA().toString());
    }

    private void linkDFAFinalGroupings() {
        DFAMinimized = new Graph();
        minimizedDFATransTable = new HashMap<>();
        for (String string :  finalGroupings.keySet()) {
            Node node = new Node();
            if (DfaUtility.isEndGroupings(finalGroupings.get(string)))
                node.setEnd(true);
            minimizedDFATransTable.put(string,node);
        }
        String initialNodeGroupId = DfaUtility.findPartitionOfNode(DFA.getInitialNode(), finalGroupings);
        Node initialNode = minimizedDFATransTable.get(initialNodeGroupId);
        initialNode.setStart(true);
        DFAMinimized.setInitialNode(initialNode); /** setting the initial node in the minimized DFA */
        for (String string : finalGroupings.keySet()) {
            Node firstNodeOfGroup = finalGroupings.get(string).get(0); /** first node of each partition use it's edges to find what partition does it points to*/
            for (String s : firstNodeOfGroup.getMap().keySet()) {
              String groupingsID =  DfaUtility.findPartitionOfNode(firstNodeOfGroup.getMap().get(s).get(0), finalGroupings);
              minimizedDFATransTable.get(string).addEdge(s,minimizedDFATransTable.get(groupingsID));
            }
        }
    }

    private void minimizeDFA() {
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
        } while (!DfaUtility.isTwoGroupingsEqual(newGrouping, grouping)); /** while last groupings not the same as the new groupings continue to minimize */
        finalGroupings = newGrouping;
    }

    /**
     * NOTE: Whenever the array list is accessed it must be sorted  to maintain order to compare the lists easily
     */

    /** constructing groupings by building a partition for each node and if it isn't built before added it to the groupings */
    private HashMap<String, ArrayList<Node>> constructGroupings(HashMap<String, ArrayList<Node>> groupings) {
        HashMap<String, ArrayList<Node>> newGroupings = new HashMap<>();
        for (String string : groupings.keySet()) {
            for (Node node : groupings.get(string)) {
                String newGroupingsID = DfaUtility.createGroupingsID(node, groupings, string);
                if (newGroupings.keySet().contains(newGroupingsID)) {
                    newGroupings.get(newGroupingsID).add(node);
                    Collections.sort(newGroupings.get(newGroupingsID));
                }
                else {
                    ArrayList<Node> newPartition = new ArrayList<>();
                    newPartition.add(node);
                    newGroupings.put(newGroupingsID,newPartition);
                }
            }
        }
        return newGroupings;
    }

    public Graph getDFAMinimized() { return DFAMinimized; }

    public HashMap<String, Node> getMinimizedDFATransTable() { return minimizedDFATransTable; }
}
