package edu.put.et.stik.mm.tfind.paggr;


import java.util.ArrayList;
import java.util.List;

import edu.put.et.stik.mm.helpers.PathAggregator;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.model.topology.Tree;
import edu.put.et.stik.mm.pfnd.ConstrainedPathFinder;
import edu.put.et.stik.mm.tfind.MetricConstrainedSteinerTreeFinder;

public class ConstrainedPathAggrTreeFinder implements
		MetricConstrainedSteinerTreeFinder {

	private final ConstrainedPathFinder pathFinder;
	private final PathAggregator pathAggregator;

	public ConstrainedPathAggrTreeFinder(ConstrainedPathFinder pathFinder,
			PathAggregator pathAggregator) {
		this.pathFinder = pathFinder;
		this.pathAggregator = pathAggregator;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {				

		// Process input
		Node source = group.get(0);
		List<Node> destinations = new ArrayList<>();
		for (int i = 1; i < group.size(); ++i) {
			destinations.add(group.get(i));
		}

		// Find paths
		List<Path> paths = new ArrayList<>();
		for (Node destination : destinations) {
			
			Path path = pathFinder
					.find(graph, source, destination, constraints);
			
			if (path == null) {
				return null;
			}
			
			paths.add(path);
		}

		// Aggregate paths
		Tree result = pathAggregator.aggregate(graph, source, paths);

		return result;
	}
}
