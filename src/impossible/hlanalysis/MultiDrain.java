package impossible.hlanalysis;

import impossible.dal.InputGraphStreamer;
import impossible.dal.NewFormatGraphStreamer;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.TopologyAnalyser;
import impossible.helpers.TopologyAnalyserImpl;
import impossible.helpers.cstrch.FengGroupConstraintsChooser;
import impossible.helpers.cstrch.GroupConstraintsChooser;
import impossible.helpers.gphmut.MetricRedistribution;
import impossible.helpers.gphmut.MetricRedistributionImpl;
import impossible.helpers.gphmut.OspfResourceDrainer;
import impossible.helpers.gphmut.ResourceDrainer;
import impossible.helpers.gphmut.UniformDistributionParameters;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.helpers.nodegrp.NodeGroupper;
import impossible.helpers.nodegrp.RandomNodeGroupper;
import impossible.model.AdjacencyListFactory;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;
import impossible.model.Tree;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.SteinerTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MultiDrain {

	// General utilities.
	private final Random random;

	// Factories.
	private final GraphFactory graphFactory;
	private final PathFinderFactory pathFinderFactory;
	private final TreeFinderFactory treeFinderFactory;

	// Strategies.
	private final GroupConstraintsChooser constraintsChooser;
	private final NodeGroupper nodeGroupper;
	private final ResourceDrainer resourceDrainer;
	private final MetricProvider metricProvider;
	private final MetricRedistribution metricResistribution;
	private final ConstraintsComparer constraintsComparer;
	private final PathAggregator pathAggregator;

	// Finders.
	private final SpanningTreeFinder helperSpanningTreeFinder;
	private final PathFinder helperPathFinder;

	// Special utilities.
	private final TopologyAnalyser topologyAnalyser;

	// Procedure setup.
	private MultiDrainSetup setup;

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
		
		constraintsComparer = new ConstraintsComparerImpl();		

		helperSpanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		helperPathFinder = pathFinderFactory.createDijkstraIndex(0);
		
		pathAggregator = new PathAggregatorImpl(helperSpanningTreeFinder);

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
					constraints, helperPathFinder, constraintsComparer, pathAggregator);

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
