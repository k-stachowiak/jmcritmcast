package impossible.hlanalysis;

import impossible.dal.InputGraphStreamer;
import impossible.dal.NewFormatGraphStreamer;
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
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.tfind.ConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	// Finders.
	private final SpanningTreeFinder spanningTreeFinder;
	private final Map<String, ConstrainedSteinerTreeFinder> treeFinders;

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

		spanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		treeFinders = setup.getTreeFinders();

		topologyAnalyser = new TopologyAnalyserImpl(spanningTreeFinder);

		this.setup = setup;
	}

	public void run(String[] args, OutputStream out) {

		StringBuilder result = new StringBuilder();

		// Cartesian product of case variables.
		for (Integer nodeSize : setup.getNodeSizes()) {
			System.err.println("Node count: " + nodeSize);
			for (Integer criteriaCount : setup.getCriteriaCounts()) {
				System.err.println("Criteria count: " + criteriaCount);
				for (Integer groupSize : setup.getGroupSizes()) {
					System.err.println("Group size: " + groupSize);
					for (Map.Entry<String, ConstrainedSteinerTreeFinder> entry : treeFinders
							.entrySet()) {

						System.err.println("Alg: " + entry.getKey());
						
						String partialResult = experiment(nodeSize,
								criteriaCount, groupSize, setup.getGraphs(),
								entry.getKey(), entry.getValue());

						if (partialResult == null) {
							System.err.println("Experiment failed.");
							return;
						}
						
						result.append(partialResult);
					}
				}				
			}
		}

		PrintWriter printWriter = new PrintWriter(out, true);
		printWriter.print(result.toString());
		printWriter.close();
		
		System.err.println("Terminated normally");
	}

	private String experiment(Integer nodeSize, Integer criteriaCount,
			Integer groupSize, int graphs, String finderName,
			ConstrainedSteinerTreeFinder treeFinder) {

		final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(nodeSize);
		StringBuilder resultStringBuilder = new StringBuilder();

		List<UniformDistributionParameters> parameters = new ArrayList<>();
		for (int p = 0; p < criteriaCount; ++p)
			parameters.add(new UniformDistributionParameters(setup
					.getRedistributionMin(), setup.getRedistributionMax()));

		for (int g = 0; g < graphs; ++g) {

			Graph graph = inputGraphStreamer.getNext();
			graph = metricResistribution.redistUniform(graph, parameters);

			int successCount = experimentStep(graph, groupSize, treeFinder);

			resultStringBuilder.append(nodeSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(finderName);
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

	private int experimentStep(Graph graph, int groupSize,
			ConstrainedSteinerTreeFinder treeFinder) {

		int successCount = 0;
		Graph copy = graph.copy();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy)) {

			List<Node> group = nodeGroupper.group(copy, groupSize);
			List<Double> constraints = constraintsChooser.choose(copy, group);
			treeFinder.setConstraints(constraints);

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
