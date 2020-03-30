package utilities;


import model.graph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class DfaUtility {

    /**
     * get union of all inputs in a set of NFA states that doesn't go to the dead node
     */
    public static ArrayList<String> getUnionInputs(ArrayList<Node> nfaNodes) {
        ArrayList<String> possibleInputs = new ArrayList<>();
        for (Node node : nfaNodes) {
            for (String string : node.getMap().keySet()) {
                if (!possibleInputs.contains(string) && string != Constant.EPSILON) {
                    possibleInputs.add(string);
                }
            }
        }
        return possibleInputs;
    }


    /**
     * get union of T & all nodes reachable from the set of nodes in T through an edge epsilon
     */
    public static ArrayList<Node> epsilonClosure(ArrayList<Node> T) {
        Stack<Node> stack = new Stack<>();
        ArrayList<Node> epsilonClosureOut = new ArrayList<>();
        epsilonClosureOut = T;
        /** push all states of T onto stack */
        for (Node node : epsilonClosureOut) {
            stack.push(node);
        }
        /** while stack is not empty pop first element t  */
        while (!stack.empty()) {
            Node t = stack.pop();
            HashMap<String, ArrayList<Node>> neighbours = new HashMap<>();
            neighbours = t.getMap();
            /** search for all unvisited (not in epsilonClosure) nodes reachable from t through an epsilon edge */
            for (String key : neighbours.keySet()) {
                if (key == Constant.EPSILON) {
                    for (Node node : neighbours.get(key)) {
                        if (!epsilonClosureOut.contains(node)) { /** if not already in epsilonClosureOut add it and push it to the stack */
                            epsilonClosureOut.add(node);
                            stack.push(node);
                        }
                    }
                }
            }
        }

/**     System.out.println("\n************* epsilon closure\n");
 for (Node node:T) {

 System.out.println(node.getCurrentId()+"\n");
 }
 System.out.println("\n************* epsilon closure \n"+T.size());
 */
        return epsilonClosureOut;
    }


    /**
     * create DFA state ID by concatenating NFA nodes ID'S into one sorted comma separated string
     */
    public static String createDfaID(ArrayList<Node> nfaNodes) {
        int length = nfaNodes.size();
        int[] intArr = new int[length];
        for (int i = 0; i < length; i++) {
            intArr[i] = nfaNodes.get(i).getCurrentId();
        }
        Arrays.sort(intArr);
        String string = new String();
        for (int i = 0; i < length - 1; i++) {
            if (!string.contains(Integer.toString(intArr[i]))) {
                string+=Integer.toString(intArr[i]) + ",";
            }
        }
        if (!string.contains(Integer.toString(intArr[length-1]))) {
            string += Integer.toString(intArr[length - 1]);
        }
        return string;
    }


    /** Set of NFA states to which there is a transition on
     input symbol a from some state s in T. */
    public static ArrayList<Node> move(ArrayList<Node> T, String a) {
        ArrayList<Node> res = new ArrayList<>();
        for (Node node : T) {
            if (node.getMap().keySet().contains(a)) {
                for (Node nodeIterator:node.getMap().get(a)) {
                    if (!res.contains(nodeIterator))
                        res.add(nodeIterator);
                }
            }
        }
        return res;
    }
}
