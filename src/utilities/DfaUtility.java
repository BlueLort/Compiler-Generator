package utilities;

import model.graph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DfaUtility {

    /**
     * get union of all inputs in a set of NFA states that doesn't go to the dead node
     */
    public static ArrayList<String> getUnionInputs(ArrayList<Node> nfaNodes) {
        ArrayList<String> possibleInputs = new ArrayList<>();
        for (Node node : nfaNodes) {
            for (String string : node.getMap().keySet()) {
                if (!possibleInputs.contains(string) && !string.equals(Constant.EPSILON)) {
                    possibleInputs.add(string);
                }
            }
        }
        return possibleInputs;
    }

    /**
     * returns the new node's type correctly
     */
    public static String getNodeType(ArrayList<Node> nfaNodes) {
        String nodeType = "";
        int length = nfaNodes.size();
        for (int i = 0; i < length - 1; i++) {
            if (!nfaNodes.get(i).getNodeType().equals("")) {
                nodeType += nfaNodes.get(i).getNodeType(); // concating to make sure there are no errors [no multiple accept states]
            }
        }
        return nodeType;
    }

    /**
     * create DFA state ID by concatenating NFA nodes ID'S into one sorted comma separated string
     */
    public static String createUnionID(ArrayList<Node> nfaNodes) {
        int length = nfaNodes.size();
        int[] intArr = new int[length];
        for (int i = 0; i < length; i++) {
            intArr[i] = nfaNodes.get(i).getCurrentId();
        }
        Arrays.sort(intArr);
        String string = "";
        for (int i = 0; i < length - 1; i++) {
            if (!string.contains(Integer.toString(intArr[i]))) {
                string += Integer.toString(intArr[i]) + ",";
            }
        }
        if (!string.contains(Integer.toString(intArr[length - 1]))) {
            string += Integer.toString(intArr[length - 1]);
        }
        return string;
    }

    /**
     * check if a partition is in group by checking all values of the hash map which is Sorted Array lists of nodes
     */
    public static boolean isPrtInGroupings(HashMap<String, ArrayList<Node>> grouping, ArrayList<Node> partition) {
        for (ArrayList<Node> partIterator : grouping.values()) {
            if (partIterator.equals(partition))
                return true;
        }
        return false;
    }


    /**
     * create a new group ID by concatenating old group ID to the following format :
     * ",input(S0), groupID(node that edge S0 points to),...,input(Si),groupID(node that edge Si points to),......
     */
    public static String createGroupingsID(Node node, HashMap<String, ArrayList<Node>> groupings, String oldGroupingsID) {
        StringBuilder stringBuilder = new StringBuilder(oldGroupingsID);
        for (String string : node.getMap().keySet()) {
            stringBuilder.append(",");
            stringBuilder.append(string);
            for (Node nodeIterator : node.getMap().get(string)) {
                stringBuilder.append(",");
                stringBuilder.append(findPartitionOfNode(nodeIterator, groupings));
            }
        }
        return stringBuilder.toString();
    }


    /**
     * find the group which a node belongs to
     */
    public static String findPartitionOfNode(Node node, HashMap<String, ArrayList<Node>> groupings) {
        for (String string : groupings.keySet()) {
            for (Node nodeIterator : groupings.get(string)) {
                if (nodeIterator.equals(node))
                    return string;
            }
        }
        return "";
    }

    /**
     * check if the given group contains an start state
     */
    public static boolean isStartGroupings(ArrayList<Node> groupings) {
        for (Node node : groupings) {
            if (node.isStart())
                return true;
        }
        return false;
    }

    /**
     * check if the given group contains an end state
     */
    public static boolean isEndGroupings(ArrayList<Node> groupings) {
        for (Node node : groupings) {
            if (node.isEnd())
                return true;
        }
        return false;
    }

    /**
     * checks if two given grouping results are the same
     */
    public static boolean isTwoGroupingsEqual(HashMap<String, ArrayList<Node>> groupingsA, HashMap<String, ArrayList<Node>> groupingsB) {
        if (groupingsA.size() != groupingsB.size())
            return false;

        for (ArrayList<Node> partition : groupingsA.values()) {
            if (!DfaUtility.isPrtInGroupings(groupingsB, partition))
                return false;
        }
        return true;
    }


}
