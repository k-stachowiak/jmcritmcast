package edu.ppt.impossible.apps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.ppt.impossible.dal.InputGraphStreamer;
import edu.ppt.impossible.dal.NewFormatGraphStreamer;
import edu.ppt.impossible.helpers.TopologyAnalyser;
import edu.ppt.impossible.helpers.TopologyAnalyserImpl;
import edu.ppt.impossible.helpers.cstrch.FengGroupConstraintsChooser;
import edu.ppt.impossible.helpers.cstrch.GroupConstraintsChooser;
import edu.ppt.impossible.helpers.metrprov.IndexMetricProvider;
import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.helpers.nodegrp.NodeGroupper;
import edu.ppt.impossible.helpers.nodegrp.RandomNodeGroupper;
import edu.ppt.impossible.helpers.resdrain.OspfResourceDrainer;
import edu.ppt.impossible.helpers.resdrain.ResourceDrainer;
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

	private static class Setup {

		private final long randomSeed;

		private final double fengDelta;

		private final double baseBandwidth;
		private final double drainedBandwidth;

		private final int graphs;

		private final List<Integer> nodeSizes;
		private final List<Integer> criteriaCounts;
		private final List<Integer> groupSizes;

		public Setup(long randomSeed, double fengDelta, double baseBandwidth,
				double drainedBandwidth, int graphs, List<Integer> nodeSizes,
				List<Integer> criteriaCounts, List<Integer> groupSizes) {

			this.randomSeed = randomSeed;
			this.fengDelta = fengDelta;
			this.baseBandwidth = baseBandwidth;
			this.drainedBandwidth = drainedBandwidth;
			this.graphs = graphs;
			this.nodeSizes = nodeSizes;
			this.criteriaCounts = criteriaCounts;
			this.groupSizes = groupSizes;
		}

		public long getRandomSeed() {
			return randomSeed;
		}

		public double getFengDelta() {
			return fengDelta;
		}

		public double getBaseBandwidth() {
			return baseBandwidth;
		}

		public double getDrainedBandwidth() {
			return drainedBandwidth;
		}

		public int getGraphs() {
			return graphs;
		}

		public List<Integer> getNodeSizes() {
			return nodeSizes;
		}

		public List<Integer> getCriteriaCounts() {
			return criteriaCounts;
		}

		public List<Integer> getGroupSizes() {
			return groupSizes;
		}
	}

	// General utilities.
	final Random random;

	// Factories.
	final GraphFactory graphFactory;
	final PathFinderFactory pathFinderFactory;
	final TreeFinderFactory treeFinderFactory;

	// Strategies.
	final GroupConstraintsChooser constraintsChooser;
	final NodeGroupper nodeGroupper;
	final ResourceDrainer resourceDrainer;
	final MetricProvider metricProvider;

	// Finders.
	final SpanningTreeFinder helperSpanningTreeFinder;
	final PathFinder helperPathFinder;

	// Special utilities.
	final TopologyAnalyser topologyAnalyser;

	// Procedure setup.
	Setup setup;

	private MultiDrain(Setup setup) {

		random = new Random(setup.getRandomSeed());
		graphFactory = new AdjacencyListFactory();
		pathFinderFactory = new PathFinderFactoryImpl();
		treeFinderFactory = new TreeFinderFactoryImpl();

		constraintsChooser = new FengGroupConstraintsChooser(
				setup.getFengDelta(), pathFinderFactory);

		nodeGroupper = new RandomNodeGroupper(random);

		resourceDrainer = new OspfResourceDrainer(setup.getBaseBandwidth(),
				setup.getDrainedBandwidth(), graphFactory);

		metricProvider = new IndexMetricProvider(0);

		helperSpanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		helperPathFinder = pathFinderFactory.CreateDijkstraIndex(0);

		topologyAnalyser = new TopologyAnalyserImpl(helperSpanningTreeFinder);

		this.setup = setup;
	}

	private void run(String[] args) {

		StringBuilder result = new StringBuilder();

		for (Integer nodeSize : setup.getNodeSizes())
			for (Integer criteriaCount : setup.getCriteriaCounts())
				for (Integer groupSize : setup.getGroupSizes()) {

					String partialResult = experiment(nodeSize, criteriaCount,
							groupSize, setup.getGraphs());

					if (partialResult == null) {
						System.err.println("Experiment failed.");
						return;
					}

					result.append(partialResult);
				}

		System.out.print(result.toString());
	}

	private String experiment(Integer nodeSize, Integer criteriaCount,
			Integer groupSize, int graphs) {

		final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(nodeSize);
		StringBuilder resultStringBuilder = new StringBuilder();

		for (int g = 0; g < graphs; ++g) {

			final Graph graph = inputGraphStreamer.getNext();

			int successCount = experimentStep(graph, groupSize);

			resultStringBuilder.append(nodeSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(criteriaCount);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(successCount);
			resultStringBuilder.append('\n');
		}

		return resultStringBuilder.toString();
	}

	private int experimentStep(Graph graph, int groupSize) {

		int successCount = 0;
		Graph copy = graph.copy();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy)) {

			List<Node> group = nodeGroupper.group(copy, groupSize);
			List<Double> constraints = constraintsChooser.choose(copy, group);
			SteinerTreeFinder treeFinder = treeFinderFactory.createPathAggr(
					constraints, helperPathFinder, helperSpanningTreeFinder);

			Tree tree = treeFinder.find(copy, group);
			if (tree == null)
				break;

			++successCount;
			copy = resourceDrainer.drain(copy, tree);

			// Debug graph cost.
			List<Double> metrics = topologyAnalyser.sumGraphMetrics(copy);
			for (Double metric : metrics) {
				System.out.print(metric);
				System.out.print('\t');
			}
			System.out.println();
		}

		return successCount;
	}

	private InputGraphStreamer prepareGraphStreamer(int nodeSize) {

		final String TOPOLOGIES_DIRECTORY = "data/new";
		final String TOPOLOGY = "ASBarabasi";
		final int NUM_GRAPHS = 1000;

		String topologyFilename = TOPOLOGIES_DIRECTORY + '/' + TOPOLOGY + '_'
				+ nodeSize + '_' + NUM_GRAPHS;

		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(
					new FileReader(topologyFilename));
		} catch (FileNotFoundException exception) {
			return null;
		}

		InputGraphStreamer inputGraphStreamer = new NewFormatGraphStreamer(
				nodeSize, NUM_GRAPHS, graphFactory, bufferedReader);

		return inputGraphStreamer;
	}

	public static void main(String[] args) {

		long randomSeed = 1L;
		double fengDelta = 0.9;
		double baseBandwidth = 10000.0;
		double drainedBandwidth = 100.0;

		int graphs = 10;

		List<Integer> nodeSizes = new ArrayList<>();
		nodeSizes.add(50);

		List<Integer> criteriaCounts = new ArrayList<>();
		criteriaCounts.add(2);
		criteriaCounts.add(3);

		List<Integer> groupSizes = new ArrayList<>();
		groupSizes.add(8);

		Setup setup = new Setup(randomSeed, fengDelta, baseBandwidth,
				drainedBandwidth, graphs, nodeSizes, criteriaCounts, groupSizes);

		new MultiDrain(setup).run(args);
	}

}
