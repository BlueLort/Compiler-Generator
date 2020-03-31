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
		Graph clonedGraph = new Graph(graph);
		
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
		Graph clonedGraph = new Graph(graph);

		newGraph.getInitialNode().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());
		clonedGraph.getInitialNode().setStart(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, newGraph.getDestination());
		clonedGraph.getDestination().setEnd(false);
		clonedGraph.getDestination().addEdge(Constant.EPSILON, clonedGraph.getInitialNode());

		return newGraph;
	}

	public static Graph concatenate(Graph firstGraph, Graph secondGraph) {
		Graph first = new Graph(firstGraph);
		Graph second = new Graph(secondGraph);

		first.getDestination().addEdge(Constant.EPSILON, second.getInitialNode());
		first.getDestination().setEnd(false);
		second.getInitialNode().setStart(false);
		first.setDestination(second.getDestination());
		return first;
	}

}
