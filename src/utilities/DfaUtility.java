package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import model.lexical_analyzer.graph.Node;

public class DfaUtility {

    /**
     * get union of all inputs in a set of NFA states that doesn't go to the dead
     * node
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
        String nodeTypes = "";
        int length = nfaNodes.size();
        for (int i = 0; i < length - 1; i++) {
            if (!nfaNodes.get(i).getNodeTypes().equals("")) {
                nodeTypes += nfaNodes.get(i).getNodeTypes();
                nodeTypes += Constant.SEPARATOR;
            }
        }
        return nodeTypes.substring(0, nodeTypes.length() > 0 ? nodeTypes.length() - 1 : 0);
    }

    /**
     * create DFA state ID by concatenating NFA nodes ID'S into one sorted comma
     * separated string
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
                string += Integer.toString(intArr[i]) + Constant.SEPARATOR;
            }
        }
        if (!string.contains(Integer.toString(intArr[length - 1]))) {
            string += Integer.toString(intArr[length - 1]);
        }
        return string;
    }

    /**
     * find the group which a node belongs to
     */
    public static Integer findPartitionOfNode(Node node, HashMap<Integer, ArrayList<Node>> groupings) {
        for (Integer groupID : groupings.keySet()) {
            for (Node nodeIterator : groupings.get(groupID)) {
                if (nodeIterator.equals(node))
                    return groupID;
            }
        }
        return -1;
    }

    /**
     * checks if a node's inputs transform to the same partitions that the group
     * nodes' inputs transform to, to know if the node fits in that group
     */
    public static boolean canFit(Node node, /** node I need to fit in a partition */
                                 Node newGroupingNode, /** partition node */
                                 HashMap<Integer, Integer> nodeParent) /** nodeParent with respect to old groupings */
    {

        if (!nodeParent.get(newGroupingNode.getCurrentId()).equals(nodeParent.get(node.getCurrentId()))
                || newGroupingNode.getMap().keySet().size() != node.getMap().keySet().size())
            return false;

        for (String input : node.getMap().keySet()) {
            if (newGroupingNode.getMap().get(input) == null)
                return false;
            Node nodeRes = node.getMap().get(input).get(0);
            Node parentRes = newGroupingNode.getMap().get(input).get(0);

            if (!nodeParent.get(nodeRes.getCurrentId()).equals(nodeParent.get(parentRes.getCurrentId()))) {
                return false;
            }
        }
        return true;
    }
}
