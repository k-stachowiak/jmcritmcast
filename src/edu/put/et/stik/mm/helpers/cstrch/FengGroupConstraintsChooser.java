package edu.put.et.stik.mm.helpers.cstrch;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.pfnd.PathFinder;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;

public class FengGroupConstraintsChooser implements GroupConstraintsChooser {

	private final double delta;
	private final PathFinderFactory pathFinderFactory;

	public FengGroupConstraintsChooser(double delta,
			PathFinderFactory pathFinderFactory) {

		this.delta = delta;
		this.pathFinderFactory = pathFinderFactory;
	}

	@Override
	public List<Double> choose(Graph graph, List<Node> group) {
		Map<Integer, Double> mins = computeMins(graph, group);
		Map<Integer, Double> maxes = computeMaxes(graph, group);		
		return interpolateMinMaxes(mins, maxes, graph.getNumMetrics());
	}

	private List<Double> interpolateMinMaxes(Map<Integer, Double> mins,
			Map<Integer, Double> maxes, int numMetrics) {
		
		List<Double> result = new ArrayList<>();
		for (int m = 1; m < numMetrics; ++m) {
			double min = mins.get(m);
			double max = maxes.get(m);
			result.add(min + delta * (max - min));
		}
		
		return result;
	}

	private Map<Integer, Double> computeMins(Graph graph, List<Node> group) {
		
		int numMetrics = graph.getNumMetrics();
		Node from = group.get(0);

		// Initialize result with positive infinities
		Map<Integer, Double> mins = new HashMap<>();
		for (int m = 1; m < numMetrics; ++m) {
			mins.put(m, Double.POSITIVE_INFINITY);
		}

		// Analyze paths to each destination
		for (int destinationIndex = 1; destinationIndex < group.size(); ++destinationIndex) {

			Node to = group.get(destinationIndex);
					
			// Check if any of the metrics is greater than current max
			for (int m = 1; m < numMetrics; ++m) {
				
				PathFinder pathFinder = pathFinderFactory.createDijkstraIndex(m);
				Path path = pathFinder.find(graph, from, to);			
				List<Double> metrics = path.getMetrics();
				
				double metric = metrics.get(m);
				double min = mins.get(m);
				
				if(metric < min) {
					mins.put(m, metric);
				}
			}
		}
		
		return mins;
	}

	private Map<Integer, Double> computeMaxes(Graph graph, List<Node> group) {

		int numMetrics = graph.getNumMetrics();
		PathFinder pathFinder = pathFinderFactory.createDijkstraIndex(0);
		Node from = group.get(0);

		// Initialize result with negative infinities
		Map<Integer, Double> maxes = new HashMap<>();
		for (int m = 1; m < numMetrics; ++m) {
			maxes.put(m, Double.NEGATIVE_INFINITY);
		}

		// Analyze paths to each destination
		for (int destinationIndex = 1; destinationIndex < group.size(); ++destinationIndex) {

			Node to = group.get(destinationIndex);
			Path path = pathFinder.find(graph, from, to);
			
			List<Double> metrics = path.getMetrics();
					
			// Check if any of the metrics is greater than current max
			for (int m = 1; m < numMetrics; ++m) {
				
				double metric = metrics.get(m);
				double max = maxes.get(m);
				
				if(metric > max) {
					maxes.put(m, metric);
				}
			}
		}
		
		return maxes;
	}
}
