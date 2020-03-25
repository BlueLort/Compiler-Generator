package model.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, ArrayList<Node>> map;
	private boolean start;
	private boolean end;
	private int currentId;
	public static int id = 0;

	public Node(boolean start, boolean end) {
		currentId = id++;
		map = new HashMap<String, ArrayList<Node>>();
		this.start = start;
		this.end = end;
	}

	public Node() {
		currentId = id++;
		map = new HashMap<String, ArrayList<Node>>();
		this.start = false;
		this.end = false;
	}

	public void addEdge(String word, Node destination) {
		if (map.containsKey(word)) { // If the character already exists in my hashmap, just add the edge
			map.get(word).add(destination);
		} else { // Else add the character and create a new edge list
			map.put(word, new ArrayList<Node>());
			map.get(word).add(destination);
		}
	}

	public void removeAllEdges(String s) {
		map.get(s).clear();
		return;
	}

	public HashMap<String, ArrayList<Node>> getMap() {
		return map;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public int getCurrentId() {
		return currentId;
	}

}