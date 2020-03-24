package model.graph;

import java.util.ArrayList;
import java.util.Map.Entry;

public class Graph {

	private Node initialNode;
	private Node destination;

	public Graph(String string) {
		initialNode = new Node(true, false);
		destination = new Node(false, true);
		initialNode.addEdge(string, destination);
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

	void DFSUtil(Node node, boolean visited[]) {
		visited[node.getCurrentId()] = true;
		System.out.print(node.getCurrentId() + " ");

		for (Entry<String, ArrayList<Node>> entry : node.getMap().entrySet()) {
			ArrayList<Node> current = entry.getValue();
			for (int i = 0; i < current.size(); i++) {
				if (!visited[current.get(i).getCurrentId()])
					DFSUtil(current.get(i), visited);
			}
		}
	}

	public void dfs() {
		boolean visited[] = new boolean[1024];
		DFSUtil(this.getInitialNode(), visited);
	}

}
