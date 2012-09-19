package impossible.helpers.gphmut;

import impossible.model.topology.Edge;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.SubGraph;

import java.util.ArrayList;
import java.util.List;


public class OspfResourceDrainer implements ResourceDrainer {

	private final double baseBandwidth;
	private final double drainedBandwidth;
	private final GraphFactory graphFactory;

	public OspfResourceDrainer(double baseBandwidth, double drainedBandwidth,
			GraphFactory graphFactory) {
		this.baseBandwidth = baseBandwidth;
		this.drainedBandwidth = drainedBandwidth;
		this.graphFactory = graphFactory;
	}

	@Override
	public Graph drain(Graph graph, SubGraph subgraph) {

		List<Edge> newEdges = new ArrayList<>();
		for (Edge edge : graph.getEdges()) {

			List<Double> newMetrics = new ArrayList<>(edge.getMetrics());

			if (subgraph.containsEdge(edge.getFrom(), edge.getTo())) {
				double translated = baseBandwidth / newMetrics.get(0);
				translated -= drainedBandwidth;

				if (translated < 1.0) {
					continue;
				}

				double retranslated = baseBandwidth / translated;
				newMetrics.set(0, retranslated);
			}

			newEdges.add(new Edge(edge.getFrom(), edge.getTo(), newMetrics));
		}

		return graphFactory.createFromLists(graph.getNodes(), newEdges);
	}

}
