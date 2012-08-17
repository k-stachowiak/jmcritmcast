package edu.ppt.impossible.apps;

import java.util.List;
import java.util.Random;

import edu.ppt.impossible.helpers.FengGroupConstraintsChooser;
import edu.ppt.impossible.helpers.GroupConstraintsChooser;
import edu.ppt.impossible.helpers.IndexMetricProvider;
import edu.ppt.impossible.helpers.MetricProvider;
import edu.ppt.impossible.helpers.NodeGroupper;
import edu.ppt.impossible.helpers.RandomNodeGroupper;
import edu.ppt.impossible.helpers.ResourceDrainer;
import edu.ppt.impossible.helpers.OspfResourceDrainer;
import edu.ppt.impossible.helpers.TopologyAnalyser;
import edu.ppt.impossible.helpers.TopologyAnalyserImpl;
import edu.ppt.impossible.model.AdjacencyListFactory;
import edu.ppt.impossible.model.Graph;
import edu.ppt.impossible.model.GraphFactory;
import edu.ppt.impossible.model.Node;
import edu.ppt.impossible.model.Tree;
import edu.ppt.impossible.pfnd.PathFinder;
import edu.ppt.impossible.pfnd.PathFinderFactory;
import edu.ppt.impossible.pfnd.PathFinderFactoryImpl;
import edu.ppt.impossible.tfind.SpanningTreeFinder;
import edu.ppt.impossible.tfind.SteinerTreeFinder;
import edu.ppt.impossible.tfind.TreeFinderFactory;
import edu.ppt.impossible.tfind.TreeFinderFactoryImpl;

public class MultiDrain {

	public static void main(String[] args) {

		// Constants.
		// ==========
		final int numNodes = 50;
		final int numCrit = 2;
		final int groupSize = 3;
		final double baseBandwidth = 10000.0;
		final double drainedBandwidth = 100.0;
		final double fengDelta = 0.9;

		// Factories.
		// ==========
		final GraphFactory graphFactory = new AdjacencyListFactory();
		final PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		final TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Helpers.
		// ========
		final Random random = new Random(System.currentTimeMillis());

		final GroupConstraintsChooser constraintsChooser = new FengGroupConstraintsChooser(
				fengDelta, pathFinderFactory);

		final NodeGroupper nodeGroupper = new RandomNodeGroupper(random);

		final ResourceDrainer resourceDrainer = new OspfResourceDrainer(
				baseBandwidth, drainedBandwidth, graphFactory);

		final MetricProvider metricProvider = new IndexMetricProvider(0);

		final SpanningTreeFinder helperSpanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		final PathFinder helperPathFinder = pathFinderFactory
				.CreateDijkstraIndex(0);

		final TopologyAnalyser topologyAnalyser = new TopologyAnalyserImpl(
				helperSpanningTreeFinder);

		// Model.
		// ======
		final Graph graph = graphFactory.createTest();

		// Procedure.
		// ==========
		int successCount = 0;
		Graph copy = graph.copy();

		StringBuilder resultStringBuilder = new StringBuilder();

		resultStringBuilder.append(numNodes);
		resultStringBuilder.append('\t');

		resultStringBuilder.append(numCrit);
		resultStringBuilder.append('\t');

		while (topologyAnalyser.isConnected(copy)) {

			List<Node> group = nodeGroupper.group(copy, groupSize);
			List<Double> constraints = constraintsChooser.choose(copy, group);
			SteinerTreeFinder treeFinder = treeFinderFactory.createPathAggr(
					constraints, helperPathFinder, helperSpanningTreeFinder);

			Tree tree = treeFinder.find(graph, group);
			if (tree == null)
				break;

			++successCount;
			copy = resourceDrainer.drain(copy, tree);
		}

		resultStringBuilder.append(successCount);

		System.out.println(resultStringBuilder.toString());
	}

}
