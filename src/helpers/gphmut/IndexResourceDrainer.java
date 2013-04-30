package helpers.gphmut;


import helpers.CostResourceTranslation;

import java.util.ArrayList;
import java.util.List;

import model.topology.Edge;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.SubGraph;


public class IndexResourceDrainer implements ResourceDrainer {

	private final CostResourceTranslation costResourceTranslation;
	private final int index;
	private final GraphFactory graphFactory;

	public IndexResourceDrainer(
			CostResourceTranslation costResourceTranslation, int index,
			GraphFactory graphFactory) {

		this.costResourceTranslation = costResourceTranslation;
		this.index = index;
		this.graphFactory = graphFactory;
	}

	@Override
	public Graph drain(Graph graph, SubGraph subgraph, double resources,
			double minResource) {

		List<Edge> newEdges = new ArrayList<>();
		for (Edge edge : graph.getEdges()) {

			List<Double> newMetrics = new ArrayList<>(edge.getMetrics());

			if (subgraph.containsEdge(edge.getFrom(), edge.getTo())) {

				double translated = costResourceTranslation
						.costToResource(newMetrics.get(index));

				double drained = translated - resources;

				if (drained < minResource) {
					continue;
				}

				double retranslated = costResourceTranslation
						.resourceToCost(drained);

				newMetrics.set(index, retranslated);
			}

			newEdges.add(new Edge(edge.getFrom(), edge.getTo(), newMetrics));
		}

		return graphFactory.createFromLists(graph.getNodes(), newEdges);
	}

}
