package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Graph {

	private Node initialNode;
	private Node destination;
	private String word;

	public Graph(String string) {
		this.word = string;
		initialNode = new Node(true, false);
		destination = new Node(false, true);
		initialNode.addEdge(string, destination);
	}

	public Graph(Graph g) {
		this.word = g.getWord();
		HashMap<Node,Node> oldToNew = new HashMap();
		this.initialNode = new Node(g.getInitialNode());
		oldToNew.put(g.getInitialNode(),this.initialNode);
		boolean[] visited = new boolean[Node.id];
		cloneDFS(g.getInitialNode(), visited, oldToNew);
		this.destination = oldToNew.get(g.getDestination());
	}

	public void cloneDFS(Node node,boolean[] visited,HashMap<Node,Node> oldToNew){
		if(visited[node.getCurrentId()]) return;
		visited[node.getCurrentId()]=true;
		for (Entry<String, ArrayList<Node>> entry : node.getMap().entrySet()) {
			ArrayList<Node> current = entry.getValue();
			for (int i = 0; i < current.size(); i++) {
				if(oldToNew.containsKey(current.get(i)) == false){
					oldToNew.put(current.get(i),new Node(current.get(i)));
				}
				if(oldToNew.get(node).getMap().containsKey(entry.getKey()) == false){
					oldToNew.get(node).getMap().put(entry.getKey(),new ArrayList<>());
				}
				oldToNew.get(node).getMap().get(entry.getKey()).add(oldToNew.get(current.get(i)));
				cloneDFS(current.get(i), visited ,oldToNew);
			}
		}
	}


	public Node getInitialNode() {
		return initialNode;
	}

	public Node getDestination() {
		return destination;
	}

	public void setInitialNode(Node initialNode) {
		this.initialNode = initialNode;
	}

	public void setDestination(Node destination) {
		this.destination = destination;
	}

	private void DFSUtil(Node node, boolean visited[]) {
		visited[node.getCurrentId()] = true;

		for (Entry<String, ArrayList<Node>> entry : node.getMap().entrySet()) {
			ArrayList<Node> current = entry.getValue();
			for (int i = 0; i < current.size(); i++) {
				if (!visited[current.get(i).getCurrentId()])
					DFSUtil(current.get(i), visited);
			}
		}
	}

	public Node DFS(Node node, int nodeID,boolean visited[]) {
		if (node.getCurrentId() == nodeID) {
			visited[node.getCurrentId()] = true;
			return node;
		}
		if (node.getMap().isEmpty()) {
			visited[node.getCurrentId()] = true;
			return null;
		}
		Node res = null;
		for (String string:node.getMap().keySet()) {
			for (Node nodeIterator:node.getMap().get(string)) {
				if (!visited[nodeIterator.getCurrentId()]) {
					res = DFS(nodeIterator,nodeID,visited);
					if (res != null) {
					visited[node.getCurrentId()] = true;
					return res;
					}
				}
			}
		}
		visited[node.getCurrentId()] = true;
		return res;
	}

	public void DFS() {
		boolean visited[] = new boolean[1024];
		DFSUtil(this.getInitialNode(), visited);
	}

	/*
	 * public Graph clone() { try { ByteArrayOutputStream outputStream = new
	 * ByteArrayOutputStream(); ObjectOutputStream outputStrm = new
	 * ObjectOutputStream(outputStream); outputStrm.writeObject(this);
	 * ByteArrayInputStream inputStream = new
	 * ByteArrayInputStream(outputStream.toByteArray()); ObjectInputStream
	 * objInputStream = new ObjectInputStream(inputStream); Graph clonedGraph =
	 * (Graph) objInputStream.readObject(); return clonedGraph;
	 * 
	 * } catch (Exception e) { e.printStackTrace(); return null; } }
	 */

	@Override
	public String toString() {
		String out = "";
		boolean visited[] = new boolean[Node.id];
		out += DFSPrintTree(initialNode, visited);
		return out;
	}

	private String DFSPrintTree(Node node, boolean visited[]) {
		if (visited[node.getCurrentId()])
			return "";
		visited[node.getCurrentId()] = true;
		String out = Integer.toString(node.getCurrentId()) + "\n";
		for (Entry<String, ArrayList<Node>> entry : node.getMap().entrySet()) {
			ArrayList<Node> current = entry.getValue();
			for (int i = 0; i < current.size(); i++) {
				String edge = entry.getKey();
				if (edge.equals("\\L"))
					edge = "eps";
				out += Integer.toString(node.getCurrentId()) + " " + Integer.toString(current.get(i).getCurrentId())
						+ " " + edge + "\n";
				System.out.println(node.getCurrentId() + " " + node.isStart() + " " + current.get(i).getCurrentId()
						+ " " + current.get(i).isEnd());
				out += DFSPrintTree(current.get(i), visited);
			}
		}
		return out;
	}

	public String getWord() {
		return word;
	}

}
