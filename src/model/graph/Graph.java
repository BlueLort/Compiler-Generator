package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Graph {

    private Node initialNode;
    private Node destination;

    public Graph(String string) {
        initialNode = new Node(true, false);
        destination = new Node(false, true);
        initialNode.addEdge(string, destination);
    }

    public Graph() {
        initialNode = new Node(true, false);
    }

    public Graph(Graph g) {
        HashMap<Node, Node> oldToNew = new HashMap();
        this.initialNode = new Node(g.getInitialNode());
        oldToNew.put(g.getInitialNode(), this.initialNode);
        boolean[] visited = new boolean[Node.id];
        cloneDFS(g.getInitialNode(), visited, oldToNew);
        this.destination = oldToNew.get(g.getDestination());

    }

    public void cloneDFS(Node node, boolean[] visited, HashMap<Node, Node> oldToNew) {
        if (visited[node.getCurrentId()]) return;
        visited[node.getCurrentId()] = true;
        for (Entry<String, ArrayList<Node>> entry : node.getMap().entrySet()) {
            ArrayList<Node> current = entry.getValue();
            for (int i = 0; i < current.size(); i++) {
                if (oldToNew.containsKey(current.get(i)) == false) {
                    oldToNew.put(current.get(i), new Node(current.get(i)));
                }
                if (oldToNew.get(node).getMap().containsKey(entry.getKey()) == false) {
                    oldToNew.get(node).getMap().put(entry.getKey(), new ArrayList<>());
                }
                oldToNew.get(node).getMap().get(entry.getKey()).add(oldToNew.get(current.get(i)));
                cloneDFS(current.get(i), visited, oldToNew);
            }
        }
    }


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
                System.out.println(node.getCurrentId() + " " + node.getNodeType() + " " + node.isStart() + " " + current.get(i).getCurrentId()
                        + " " + current.get(i).isEnd() + " " + current.get(i).getNodeType());
                out += DFSPrintTree(current.get(i), visited);
            }
        }
        return out;
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


}
