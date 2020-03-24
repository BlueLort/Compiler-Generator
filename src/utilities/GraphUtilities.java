package utilities;

import java.util.ArrayList;

import model.graph.Graph;

public class GraphUtilities {

	public static Graph or(ArrayList<Graph> graphs) {
		Graph newGraph = new Graph(Constant.epsilon);
		newGraph.getInitialNode().removeAllEdges(Constant.epsilon);

		for (Graph graph : graphs) {
			newGraph.getInitialNode().addEdge(Constant.epsilon, graph.getInitialNode());
			graph.getInitialNode().setStart(false);
			graph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
			graph.getDestination().setEnd(false);
		}
		// System.out.println("OR" + newGraph.getInitialNode().getCurrentId());
		// System.out.println("OR" + newGraph.getDestination().getCurrentId());
		return newGraph;
	}

	public static Graph kleeneClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.epsilon);

		newGraph.getInitialNode().addEdge(Constant.epsilon, graph.getInitialNode());
		graph.getInitialNode().setStart(false);
		graph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		graph.getDestination().setEnd(false);
		graph.getDestination().addEdge(Constant.epsilon, graph.getInitialNode());

		return newGraph;
	}

	public static Graph plusClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.epsilon);
		newGraph.getInitialNode().removeAllEdges(Constant.epsilon);

		newGraph.getInitialNode().addEdge(Constant.epsilon, graph.getInitialNode());
		graph.getInitialNode().setStart(false);
		graph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		graph.getDestination().setEnd(false);
		graph.getDestination().addEdge(Constant.epsilon, graph.getInitialNode());

		// System.out.println("+" + newGraph.getInitialNode().getCurrentId());
		// System.out.println("+" + newGraph.getDestination().getCurrentId());

		return newGraph;
	}

	public static Graph concatenate(Graph firstGraph, Graph secondGraph) {
		Graph newGraph = firstGraph;

		newGraph.getDestination().addEdge(Constant.epsilon, secondGraph.getInitialNode());
		newGraph.getDestination().setEnd(false);
		newGraph.setDestination(secondGraph.getDestination());
		return newGraph;
	}

}
