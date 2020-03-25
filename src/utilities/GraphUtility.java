package utilities;

import java.util.ArrayList;

import model.graph.Graph;

public class GraphUtility {

	public static Graph or(ArrayList<Graph> graphs) {
		Graph newGraph = new Graph(Constant.EPSILON);
		newGraph.getInitialNode().removeAllEdges(Constant.EPSILON);

		for (Graph graph : graphs) {
			newGraph.getInitialNode().addEdge(Constant.EPSILON, graph.getInitialNode());
			graph.getInitialNode().setStart(false);
			graph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
			graph.getDestination().setEnd(false);
		}
		// System.out.println("OR" + newGraph.getInitialNode().getCurrentId());
		// System.out.println("OR" + newGraph.getDestination().getCurrentId());
		return newGraph;
	}

	public static Graph or(Graph firstGraph, Graph secondGraph) {
		Graph newGraph = new Graph(Constant.EPSILON);
		newGraph.getInitialNode().removeAllEdges(Constant.EPSILON);

		newGraph.getInitialNode().addEdge(Constant.EPSILON, firstGraph.getInitialNode());
		firstGraph.getInitialNode().setStart(false);
		firstGraph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
		firstGraph.getDestination().setEnd(false);

		newGraph.getInitialNode().addEdge(Constant.EPSILON, secondGraph.getInitialNode());
		secondGraph.getInitialNode().setStart(false);
		secondGraph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
		secondGraph.getDestination().setEnd(false);

		return newGraph;
	}

	public static Graph kleeneClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.EPSILON);
		Graph clonedGraph = graph.deepCopy(graph);

		newGraph.getInitialNode().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());
		clonedGraph.getInitialNode().setStart(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
		clonedGraph.getDestination().setEnd(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());

		return newGraph;
	}

	public static Graph plusClosure(Graph graph) {

		Graph newGraph = new Graph(Constant.EPSILON);
		newGraph.getInitialNode().removeAllEdges(Constant.EPSILON);
		Graph clonedGraph = graph.deepCopy(graph);

		newGraph.getInitialNode().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());
		clonedGraph.getInitialNode().setStart(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
		clonedGraph.getDestination().setEnd(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());

		// System.out.println("+" + newGraph.getInitialNode().getCurrentId());
		// System.out.println("+" + newGraph.getDestination().getCurrentId());

		return newGraph;
	}

	public static Graph concatenate(Graph firstGraph, Graph secondGraph) {
		firstGraph.getDestination().addEdge(Constant.EPSILON, secondGraph.getInitialNode());
		firstGraph.getDestination().setEnd(false);
		firstGraph.setDestination(secondGraph.getDestination());
		return firstGraph;
	}

}
