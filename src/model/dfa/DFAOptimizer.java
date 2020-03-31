package model.dfa;

import model.graph.Graph;
import model.graph.Node;
import utilities.DfaUtility;

import javax.naming.NamingEnumeration;
import java.util.*;

public class DFAOptimizer {
    Graph DFA;
    Graph DFAMinimized;
    HashMap<String, Node> DFATransTable;
    HashMap<String, ArrayList<Node>> finalGroup;

    public DFAOptimizer(DFA DFA) {
        this.DFA = DFA.getDFA();
        this.DFATransTable = DFA.getDFATransTable();
        minimizeDFA();
    }

    private void minimizeDFA() {
        HashMap<String, ArrayList<Node>> group = new HashMap<>();
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
        group.put("start", nonAcceptingState);
        group.put("final", acceptingState);
        HashMap<String, ArrayList<Node>> newGroup = group;
        do {
            group = newGroup;
            newGroup = constructGroup(group);
        } while (!isGroupsEqual(newGroup, group));
        finalGroup = newGroup;
        System.out.println("final group finished");
        System.out.println(newGroup.size());
    }


    /**
     * sort array list of each group
     */
    private HashMap<String, ArrayList<Node>> constructGroup(HashMap<String, ArrayList<Node>> group) {
        HashMap<String, ArrayList<Node>> newGroup = new HashMap<>();
        for (String string : group.keySet()) {
            for (Node node : group.get(string)) {
                String newGroupID = createGroupID(node, group, string);
                if (newGroup.keySet().contains(newGroupID)) {
                    newGroup.get(newGroupID).add(node);
                    Collections.sort(newGroup.get(newGroupID));
                }
                else {
                    ArrayList<Node> newPartition = new ArrayList<>();
                    newPartition.add(node);
                    newGroup.put(newGroupID,newPartition);
                }
            }
        }
        return newGroup;
    }

    public Graph getDFAMinimized() {
        return DFAMinimized;
    }

    public HashMap<String, Node> getDFATransTable() {
        return DFATransTable;
    }

    private boolean isGroupsEqual(HashMap<String, ArrayList<Node>> groupA, HashMap<String, ArrayList<Node>> groupB) {
        if (groupA.size() != groupB.size())
            return false;

        for (ArrayList<Node> partition : groupA.values()) {
            if (!isPrtInGroup(groupB, partition))
                return false;
        }
        return true;
    }

    private String createGroupID(Node node, HashMap<String, ArrayList<Node>> group, String oldGroupID) {
        StringBuilder stringBuilder = new StringBuilder(oldGroupID);
        for (String string : node.getMap().keySet()) {
            stringBuilder.append(string);
            stringBuilder.append(",");
            for (Node nodeIterator : node.getMap().get(string)) {
                if (node.equals(nodeIterator)) {
                    stringBuilder.append(findGroupOfNode(nodeIterator,group));
                    break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private String findGroupOfNode(Node node, HashMap<String , ArrayList<Node>> group) {
        for (String string : group.keySet()) {
            for (Node nodeIterator: group.get(string)) {
                if (nodeIterator.equals(node))
                    return string;
            }
        }
        return "";
    }

    private boolean isPrtInGroup(HashMap<String, ArrayList<Node>> group, ArrayList<Node> partition) {
        for (ArrayList<Node> partIterator : group.values()) {
            if (partIterator.equals(partition))
                return true;
        }
        return false;
    }


}
