package edu.ppt.impossible.hlanalysis;

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
import edu.ppt.impossible.helpers.gphmut.MetricRedistribution;
import edu.ppt.impossible.helpers.gphmut.MetricRedistributionImpl;
import edu.ppt.impossible.helpers.gphmut.OspfResourceDrainer;
import edu.ppt.impossible.helpers.gphmut.ResourceDrainer;
import edu.ppt.impossible.helpers.gphmut.UniformDistributionParameters;
import edu.ppt.impossible.helpers.metrprov.IndexMetricProvider;
import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.helpers.nodegrp.NodeGroupper;
import edu.ppt.impossible.helpers.nodegrp.RandomNodeGroupper;
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
	final MetricRedistribution metricResistribution;

	// Finders.
	final SpanningTreeFinder helperSpanningTreeFinder;
	final PathFinder helperPathFinder;

	// Special utilities.
	final TopologyAnalyser topologyAnalyser;

	// Procedure setup.
	MultiDrainSetup setup;

	public MultiDrain(MultiDrainSetup setup) {

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

		metricResistribution = new MetricRedistributionImpl(graphFactory,
				random);

		helperSpanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		helperPathFinder = pathFinderFactory.CreateDijkstraIndex(0);

		topologyAnalyser = new TopologyAnalyserImpl(helperSpanningTreeFinder);

		this.setup = setup;
	}

	public void run(String[] args) {

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

		List<UniformDistributionParameters> parameters = new ArrayList<>();
		for (int p = 0; p < criteriaCount; ++p)
			parameters.add(new UniformDistributionParameters(setup
					.getRedistributionMin(), setup.getRedistributionMax()));

		for (int g = 0; g < graphs; ++g) {

			Graph graph = inputGraphStreamer.getNext();
			graph = metricResistribution.redistUniform(graph, parameters);

			int successCount = experimentStep(graph, groupSize);

			resultStringBuilder.append(nodeSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(criteriaCount);
			resultStringBuilder.append('\t');
			
			resultStringBuilder.append(groupSize);
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
		}

		return successCount;
	}

	private InputGraphStreamer prepareGraphStreamer(int nodeSize) {

		String topologyFilename = setup.getTopologiesDirectory() + '/'
				+ setup.getTopology() + '_' + nodeSize + '_'
				+ setup.getGraphsInFile();

		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader(
					new FileReader(topologyFilename));
		} catch (FileNotFoundException exception) {
			return null;
		}

		InputGraphStreamer inputGraphStreamer = new NewFormatGraphStreamer(
				nodeSize, setup.getGraphsInFile(), graphFactory, bufferedReader);

		return inputGraphStreamer;
	}

}
