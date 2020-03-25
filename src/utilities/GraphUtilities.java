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

	public static Graph or(Graph firstGraph, Graph secondGraph) {
		Graph newGraph = new Graph(Constant.epsilon);
		newGraph.getInitialNode().removeAllEdges(Constant.epsilon);

		newGraph.getInitialNode().addEdge(Constant.epsilon, firstGraph.getInitialNode());
		firstGraph.getInitialNode().setStart(false);
		firstGraph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		firstGraph.getDestination().setEnd(false);

		newGraph.getInitialNode().addEdge(Constant.epsilon, secondGraph.getInitialNode());
		secondGraph.getInitialNode().setStart(false);
		secondGraph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		secondGraph.getDestination().setEnd(false);

		return newGraph;
	}

	public static Graph kleeneClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.epsilon);
		Graph clonedGraph = graph.deepCopy(graph);

		newGraph.getInitialNode().addEdge(Constant.epsilon, clonedGraph.getInitialNode());
		clonedGraph.getInitialNode().setStart(false);
		clonedGraph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		clonedGraph.getDestination().setEnd(false);
		clonedGraph.getDestination().addEdge(Constant.epsilon, clonedGraph.getInitialNode());

		return newGraph;
	}

	public static Graph plusClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.epsilon);
		newGraph.getInitialNode().removeAllEdges(Constant.epsilon);
		Graph clonedGraph = graph.deepCopy(graph);

		newGraph.getInitialNode().addEdge(Constant.epsilon, clonedGraph.getInitialNode());
		clonedGraph.getInitialNode().setStart(false);
		clonedGraph.getDestination().addEdge(Constant.epsilon, newGraph.getDestination());
		clonedGraph.getDestination().setEnd(false);
		clonedGraph.getDestination().addEdge(Constant.epsilon, clonedGraph.getInitialNode());

		// System.out.println("+" + newGraph.getInitialNode().getCurrentId());
		// System.out.println("+" + newGraph.getDestination().getCurrentId());

		return newGraph;
	}

	public static Graph concatenate(Graph firstGraph, Graph secondGraph) {
		firstGraph.getDestination().addEdge(Constant.epsilon, secondGraph.getInitialNode());
		firstGraph.getDestination().setEnd(false);
		firstGraph.setDestination(secondGraph.getDestination());
		return firstGraph;
	}

}
