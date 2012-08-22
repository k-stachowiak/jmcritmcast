package impossible.helpers;

import impossible.model.EdgeDefinition;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.model.SubGraph;
import impossible.model.SubGraphToGraphAdapter;
import impossible.model.Tree;
import impossible.tfind.SpanningTreeFinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PathAggregatorImpl implements PathAggregator {

	private final SpanningTreeFinder spanningTreeFinder;

	public PathAggregatorImpl(SpanningTreeFinder spanningTreeFinder) {
		this.spanningTreeFinder = spanningTreeFinder;
	}

	@Override
	public Tree aggregate(Graph graph, Node root, List<Path> paths) {

		// Allocate defining objects.
		Set<Integer> nodesSet = new HashSet<>();
		Set<EdgeDefinition> edgeDefinitionsSet = new HashSet<>();

		// Process edges.
		for (Path path : paths)
			for (EdgeDefinition edgeDefinition : path.getEdgeDefinitions()) {

				nodesSet.add(edgeDefinition.getFrom());
				nodesSet.add(edgeDefinition.getTo());
				edgeDefinitionsSet.add(edgeDefinition);
			}

		// Build lists from sets.
		List<Integer> nodes = new ArrayList<>(nodesSet);
		List<EdgeDefinition> edgeDefinitions = new ArrayList<>(
				edgeDefinitionsSet);

		// Validate acquired data.
		if (nodes.isEmpty())
			return null;

		// Build the incomplete subgraph.
		SubGraph ripley7 = new SubGraph(graph, nodes, edgeDefinitions);
		SubGraphToGraphAdapter subGraphToGraphAdapter = new SubGraphToGraphAdapter(
				ripley7);

		// Remove cycles and return.
		return spanningTreeFinder.find(root, subGraphToGraphAdapter);

	}
}
