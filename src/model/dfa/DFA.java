package model.dfa;


import model.graph.*;
import utilities.DfaUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;




public class DFA {
    Graph                  NFACombined;
    Graph                  DFA;
    Stack<String>          dfaStatesUnmarked;
    HashMap<String,Node>   DfatransTable;




    public DFA(Graph NFACombined) {
        DFA = new Graph("DFA");
        this.NFACombined = NFACombined;
        dfaStatesUnmarked = new Stack<>();
        ArrayList<Node> s0 = new ArrayList<>();
        s0.add(NFACombined.getInitialNode());
        ArrayList<Node> epsClosureS0 = DfaUtility.epsilonClosure(s0);
        dfaStatesUnmarked.push(DfaUtility.createDfaID(epsClosureS0));
        constructDFA(epsClosureS0);
        minimizeDfa();
    }


    private void minimizeDfa() {

    }


    private void constructDFA(ArrayList<Node> epsClosureS0) {
        while (!dfaStatesUnmarked.empty()) {

        }
    }

    public Graph getDFA() {
        return DFA;
    }

    /** to check if a state  is already visited */
    private boolean isInUnmarked(Node node) {
        if (dfaStatesUnmarked.contains(node.getDfaNodeID()))
            return true;
        return false;
    }

    /** to check if a state  is already visited or explored */
    private boolean isInDstates(Node node) {
            if (DfatransTable.keySet().contains(node.getDfaNodeID()))
                return true;
        return false;
    }
}


