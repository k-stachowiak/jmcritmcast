package edu.put.et.stik.mm.helpers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.put.et.stik.mm.model.topology.Edge;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Tree;
import edu.put.et.stik.mm.tfind.SpanningTreeFinder;


public class TopologyAnalyserImpl implements TopologyAnalyser {

	@Override
	public boolean isConnected(Graph graph, SpanningTreeFinder spanningTreeFinder) {

		if (graph.getNumNodes() == 0)
			return true;

		Tree spanningTree = spanningTreeFinder.find(graph.getNodes().get(0),
				graph);

		return graph.getNumNodes() == spanningTree.getNumNodes();
	}

	@Override
	public List<Double> sumGraphMetrics(Graph graph) {

		List<Double> result = new ArrayList<>();
		for (int m = 0; m < graph.getNumMetrics(); ++m) {
			result.add(0.0);
		}

		for (Edge edge : graph.getEdges()) {
			for (int m = 0; m < graph.getNumMetrics(); ++m) {
				result.set(m, result.get(m) + edge.getMetrics().get(m));
			}
		}

		return result;
	}
	
	@Override
	public boolean equal(Graph a, Graph b) {		
		Set<Node> aNodes = new HashSet<>(a.getNodes());
		Set<Node> bNodes = new HashSet<>(b.getNodes());
		if(!aNodes.equals(bNodes)) return false;
		
		Set<Edge> aEdges = new HashSet<>(a.getEdges());
		Set<Edge> bEdges = new HashSet<>(b.getEdges());		
		if(!aEdges.equals(bEdges)) return false;
		
		return true;
	}
}
