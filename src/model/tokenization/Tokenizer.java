package model.tokenization;

import javafx.util.Pair;
import model.dfa.DFAOptimizer;
import model.graph.Graph;
import model.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tokenizer {
    private Graph minimalDFA;

    private HashMap<String,Pair<Node,String>> transitionTable; //currentNode,input -> nextNode,Output
    public Tokenizer(DFAOptimizer OptimizedDFA){
        this.minimalDFA = OptimizedDFA.getDFAMinimized();
        HashMap<Pair< Pair<Node,Node> ,String>,String> finalStates = OptimizedDFA.getFinalStates();
        transitionTable = new HashMap<>();
        constructTransitionTable(finalStates);
    }
    private void constructTransitionTable(HashMap<Pair< Pair<Node,Node> ,String>,String> finalStates){
        for(Map.Entry<Pair< Pair<Node,Node> ,String>,String> entry:finalStates.entrySet()){
            transitionTable.put(Integer.toString(entry.getKey().getKey().getKey().getCurrentId())+","+entry.getKey().getValue()
                                ,new Pair(entry.getKey().getKey().getValue(),entry.getValue()));
        }
    }
    public ArrayList<String> getTokens(String input){
        ArrayList<String> lexems = new ArrayList<>();
        ArrayList<String> matches = new ArrayList<>();
        Node start = minimalDFA.getInitialNode();
        int idx = 0;
        int retValue;
        do{
            retValue = addGenerations(input,idx,lexems,start);
            if(retValue == -1)return null;

            if(retValue == -2) idx++;
            else{
                matches.add(input.substring(idx,retValue+1));
                idx = retValue + 1;
            }
        }while(idx < input.length());
        //TODO CREATE INFO WINDOW
        return lexems;
    }

    private int addGenerations(String input,int idx,ArrayList<String> lexems,Node currNode){
        char currentChar = input.charAt(idx);
        if(currentChar == ' ' || currentChar == '\n' || idx >= input.length()) return -2;
        String transition = Integer.toString(currNode.getCurrentId()) + "," + input.charAt(idx);
        Pair<Node,String> nextTransition = transitionTable.get(transition);
        if(nextTransition != null) {
                String acceptance = nextTransition.getValue();
                int retValue = addGenerations(input, idx + 1, lexems, nextTransition.getKey());
                if (retValue == -1 || retValue == -2) {
                    if(acceptance.equals("")) return -1;
                    lexems.add(acceptance);
                    return idx;
                } else {
                    return retValue;
                }
        }
        return -1;
    }


    /** Helper Function **/
    //returns event on moving from node to node on certain input
//    private String getEvent(Node source,Node destination,String input){
//        Pair<Pair<Node,Node>,String> required = new Pair(new Pair(source,destination),input);
//        if(transitionTable.containsKey(required)){
//            return transitionTable.get(required);
//        }
//        return null;
//    }


    /** return next state given from a node and input char if found
     * else null is returned */
    private static Node getNextState(HashMap<String,Node> transitionTable, int nodeID, String input) {
        Node node = transitionTable.get(Integer.toString(nodeID));
        if (node == null)
            return null;
        if (node.getMap().get(input) != null)
            return  node.getMap().get(input).get(0);
        return null;
    }


}
