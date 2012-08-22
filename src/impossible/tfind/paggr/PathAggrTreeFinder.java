package impossible.tfind.paggr;

import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.model.SubGraph;
import impossible.model.SubGraphToGraphAdapter;
import impossible.model.Tree;
import impossible.pfnd.PathFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.SteinerTreeFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PathAggrTreeFinder implements SteinerTreeFinder {

	private final List<Double> constraints;
	private final PathFinder pathFinder;
	private final SpanningTreeFinder spanningTreeFinder;

	public PathAggrTreeFinder(List<Double> constraints, PathFinder pathFinder,
			SpanningTreeFinder spanningTreeFinder) {
		this.constraints = constraints;
		this.pathFinder = pathFinder;
		this.spanningTreeFinder = spanningTreeFinder;
	}

	@Override
	public Tree find(Graph graph, List<Node> group) {

		// Process input
		Node source = group.get(0);
		List<Node> destinations = new ArrayList<>();
		for (int i = 1; i < group.size(); ++i)
			destinations.add(group.get(i));

		// Find paths
		List<Path> paths = new ArrayList<>();
		for (Node destination : destinations) {
			Path path = pathFinder.find(graph, source, destination);
			if (path == null)
				return null;
			paths.add(path);
		}

		// Aggregate paths
		Set<Integer> nodesSet = new HashSet<>();
		Set<SubGraph.EdgeDefinition> edgeDefinitionsSet = new HashSet<>();
		for (Path path : paths)
			if (fulfillsConstraints(path, constraints))
				for (SubGraph.EdgeDefinition edgeDefinition : path
						.getEdgeDefinitions()) {

					nodesSet.add(edgeDefinition.getFrom());
					nodesSet.add(edgeDefinition.getTo());
					edgeDefinitionsSet.add(edgeDefinition);
				}

		List<SubGraph.EdgeDefinition> edgeDefinitions = new ArrayList<>(
				edgeDefinitionsSet);

		List<Integer> nodes = new ArrayList<>(nodesSet);

		if (nodes.isEmpty())
			return null;

		SubGraph ripley7 = new SubGraph(graph, nodes, edgeDefinitions);
		SubGraphToGraphAdapter subGraphToGraphAdapter = new SubGraphToGraphAdapter(
				ripley7);

		// Prune
		return spanningTreeFinder.find(source, subGraphToGraphAdapter);
	}

	private boolean fulfillsConstraints(Path path, List<Double> constraints2) {
		List<Double> metrics = path.getMetrics();
		for (int m = 1; m < metrics.size(); ++m) {
			if (metrics.get(m) > constraints.get(m - 1))
				return false;
		}
		return true;
	}
}
