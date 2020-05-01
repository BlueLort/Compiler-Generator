package utilities;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class TrieNode {

    private ArrayList<String> nodeKeys; // keys of the node sorted in order of input reading
    private HashMap<String,Pair<TrieNode,Integer>> currentSet; //Integer is frequency of the string
    private boolean isEnd;
    public TrieNode(){
        nodeKeys = new ArrayList<>();
        currentSet = new HashMap<>();
        isEnd = false;
    }

    public ArrayList<String> getNodeKeys() {
        return nodeKeys;
    }

    public Pair<TrieNode,Integer> getNode(String key) {
        return currentSet.get(key);
    }
    public boolean isEnd() {
        return isEnd;
    }
    public void addString(ArrayList<String> words,int idx){
        if(idx >= words.size()){
            isEnd = true;
            return;
        }
        if(!currentSet.containsKey(words.get(idx))){
            TrieNode nextNode = new TrieNode();
            currentSet.put(words.get(idx),new Pair<>(nextNode,1));
            nodeKeys.add(words.get(idx));
        }else{
            Pair<TrieNode,Integer> nextInfo =  currentSet.get(words.get(idx));
            currentSet.put(words.get(idx),new Pair<>(nextInfo.getKey(),nextInfo.getValue() + 1));
        }
        currentSet.get(words.get(idx)).getKey().addString(words,idx + 1);
    }
}
