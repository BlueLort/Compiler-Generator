package model.dfa;

import model.graph.*;
import utilities.DfaUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DFA {
    private Graph                   NFACombined;
    private Graph                   DFA;
    private Stack<ArrayList<Node>>  DFAStatesUnmarked;
    private HashMap<String,Node>    DFATransTable;

    public DFA(Graph NFACombined) {
        DFA = new Graph();
        this.NFACombined = NFACombined;
        DFAStatesUnmarked = new Stack<>();
        DFATransTable = new HashMap<>();
        ArrayList<Node> s0 = new ArrayList<>();
        s0.add(NFACombined.getInitialNode());
        ArrayList<Node> epsClosureS0 = DfaUtility.epsilonClosure(s0);
        DFA.getInitialNode().setDfaNodeID(DfaUtility.createUnionID(epsClosureS0));
        DFAStatesUnmarked.push(epsClosureS0);
        DFATransTable.put(DfaUtility.createUnionID(epsClosureS0),DFA.getInitialNode());
        constructDFA();
    }



    private void constructDFA() {
        while (!DFAStatesUnmarked.empty()) {
            ArrayList<Node> T = DFAStatesUnmarked.pop();
            String TsID = DfaUtility.createUnionID(T);
            ArrayList<Node> U;
            for (String a : DfaUtility.getUnionInputs(T)) {
                U = DfaUtility.epsilonClosure(DfaUtility.move(T,a));
                String newID = DfaUtility.createUnionID(U);
                if (!DFATransTable.containsKey(newID)) {
                    DFAStatesUnmarked.push(U);
                    Node node = new Node();
                    DFATransTable.put(newID,node);
                    if(U.contains(NFACombined.getDestination()))
                        node.setEnd(true);
                }
                DFATransTable.get(TsID).addEdge(a, DFATransTable.get(newID));
            }
        }
    }

    public Graph getDFA() { return DFA; }

    public HashMap<String,Node> getDFATransTable() { return DFATransTable; }
}


