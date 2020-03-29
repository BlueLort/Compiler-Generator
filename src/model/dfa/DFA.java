package model.dfa;


import model.graph.*;
import utilities.DfaUtility;
import java.util.ArrayList;
import java.util.Stack;



public class DFA {
    Graph NFACombined;
    Graph DFA;
    Stack<String> dfaStatesUnmarked;   /** to check if a state or a subset of a state is already visited or explored */
    int unmarked;


    public DFA(Graph NFACombined) {
        DFA = new Graph("DFA");
        this.NFACombined = NFACombined;
        dfaStatesUnmarked = new Stack<>();
        ArrayList<Node> s0 = new ArrayList<>();
        s0.add(NFACombined.getInitialNode());
        ArrayList<Node> epsClosureS0 = DfaUtility.epsilonClosure(s0);
        dfaStatesUnmarked.push(DfaUtility.createDfaID(epsClosureS0));
        constructDFA();
        minimizeDfa();
    }


    private void minimizeDfa(){

    }


    private void constructDFA() {
        while (!dfaStatesUnmarked.empty()) {

        }
    }

    public Graph getDFA() {
        return DFA;
    }
}


