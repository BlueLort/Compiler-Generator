package model.dfa;


import model.graph.*;
import utilities.Constant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * OPERATION DESCRIPTION
 * e-closure(s) Set of NFA states reachable from NFA state s
 * on e-transitions alone.
 * e-closure(T) Set of NFA states reachable from some NFA state s
 * in set T on e-transitions alone; = Us in T e-closure(s).
 * move(T, a) Set of NFA states to which there is a transition on
 * input symbol a from some state s in T.
 */


/**initially, e-closure(s0) is the only state in Dstates, and it is unmarked;
 *  while ( there is an unmarked state T in Dstates ) {
 *          mark T;
 *          for ( each input symbol a ) {
 *          U = e-closure(move(T,a));
 *          if ( U is not in Dstates )
 *                   add U as an unmarked state to Dstates;
 *          Dtran[T, a] = U;
 *          }
 *  }
 **/

public class DFA {
    Graph NFACombined;
    Graph DFA;
    Graph minimizedDFA;
    HashMap<ArrayList<Integer>,Boolean> dfaStatesVisited;   /** to check if a state or a subset of a state is already visited or explored */

    public DFA(Graph NFACombined){
        DFA = new Graph("");
        this.NFACombined = NFACombined;
        dfaStatesVisited = new HashMap<>();
        ArrayList<Integer> s0 = new ArrayList<>();
        s0.add(NFACombined.getInitialNode().getCurrentId());
        dfaStatesVisited.put(s0,false);
        constructDFA();
        minimizeDfa();
    }


    public void minimizeDfa(){

    }


    private void constructDFA() {

    }

    private static ArrayList<Node> epsilonClosure(ArrayList<Node> T, Graph graph) {
        Stack<Node> stack = new Stack<>();
        ArrayList<Node> epsilonClosureOut = new ArrayList<>();
        epsilonClosureOut = T;
        /** push all states of T onto stack */
        for (Node node: epsilonClosureOut) {
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
}


