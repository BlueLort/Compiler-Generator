package model.dfa;

import model.graph.*;
import utilities.DfaUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DFA {
    Graph                   NFACombined;
    Graph                   DFA;
    Stack<ArrayList<Node>>  dfaStatesUnmarked;
    HashMap<String,Node>    DfatransTable;

    public DFA(Graph NFACombined) {
        DFA = new Graph("DFA");
        this.NFACombined = NFACombined;
        dfaStatesUnmarked = new Stack<>();
        ArrayList<Node> s0 = new ArrayList<>();
        s0.add(NFACombined.getInitialNode());
        ArrayList<Node> epsClosureS0 = DfaUtility.epsilonClosure(s0);
        DFA.getInitialNode().setDfaNodeID(DfaUtility.createDfaID(epsClosureS0));
        dfaStatesUnmarked.push(epsClosureS0);
        DfatransTable.put(DfaUtility.createDfaID(epsClosureS0),DFA.getInitialNode());
        constructDFA();
        minimizeDfa();
    }

    private void minimizeDfa() {

    }

    private void constructDFA() {
        while (!dfaStatesUnmarked.empty()) {
            ArrayList<Node> T = dfaStatesUnmarked.pop();
            String TsID = DfaUtility.createDfaID(T);
            ArrayList<Node> U = new ArrayList<>();
            for (String a:DfaUtility.getUnionInputs(T)) {
                U = DfaUtility.epsilonClosure(DfaUtility.move(T,a));
                String newID = DfaUtility.createDfaID(U);
                if (!DfatransTable.containsKey(newID)) {
                    dfaStatesUnmarked.push(U);
                    Node node = new Node();
                    DfatransTable.put(newID,node);
                }
                DfatransTable.get(TsID).addEdge(a,DfatransTable.get(newID));
            }
        }
    }

    public Graph getDFA() {
        return DFA;
    }
}


