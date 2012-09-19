package impossible.apps;

import impossible.dal.DTOMarshaller;
import impossible.dal.InputGraphStreamer;
import impossible.dal.NewFormatGraphStreamer;
import impossible.dto.ConstrainedTreeFindProblemDTO;
import impossible.dto.GraphDTO;
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
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import impossible.pfnd.mlarac.IntersectLambdaEstimator;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;
import impossible.tfind.ConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MultiDrainLogic {

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

	// Logging.
	private List<ConstrainedTreeFindProblemDTO> failedProblems;

	public MultiDrainLogic(MultiDrainSetup setup) {

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
		treeFinders = allocateFinders();

		topologyAnalyser = new TopologyAnalyserImpl(spanningTreeFinder);

		this.setup = setup;
	}

	public void run(String[] args, OutputStream out, OutputStream debug) {

		failuresReset();

		StringBuilder result = new StringBuilder();

		PrintWriter debugWriter = new PrintWriter(debug, true);

		// Cartesian product of case variables.
		for (Integer nodeSize : setup.getNodeSizes()) {
			debugWriter.println("Node count: " + nodeSize);
			for (Integer criteriaCount : setup.getCriteriaCounts()) {
				debugWriter.println("Criteria count: " + criteriaCount);
				for (Integer groupSize : setup.getGroupSizes()) {
					debugWriter.println("Group size: " + groupSize);
					for (String finderName : setup.getTreeFinderNames()) {

						debugWriter.println("Alg: " + finderName);

						String partialResult = experiment(nodeSize,
								criteriaCount, groupSize, setup.getGraphs(),
								finderName, treeFinders.get(finderName));

						if (partialResult == null) {
							debugWriter.println("Experiment failed.");
							return;
						}

						result.append(partialResult);
					}
				}
			}
		}

		PrintWriter outWriter = new PrintWriter(out, true);
		outWriter.print(result.toString());
		outWriter.close();

		failuresReport();

		debugWriter.println("Terminated normally");

		debugWriter.close();
	}

	private String experiment(Integer nodeSize, Integer criteriaCount,
			Integer groupSize, int graphs, String finderName,
			ConstrainedSteinerTreeFinder treeFinder) {

		final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(nodeSize);
		if (inputGraphStreamer == null) {
			throw new RuntimeException("Failed opening graph streamer.\n");
		}

		StringBuilder resultStringBuilder = new StringBuilder();

		List<UniformDistributionParameters> parameters = new ArrayList<>();
		for (int p = 0; p < criteriaCount; ++p)
			parameters.add(new UniformDistributionParameters(setup
					.getRedistributionMin(), setup.getRedistributionMax()));

		for (int g = 0; g < graphs; ++g) {

			GraphDTO graphDTO = inputGraphStreamer.getNext();
			Graph graph = graphFactory.createFromDTO(graphDTO);
			graphDTO = null;

			graph = metricResistribution.redistUniform(graph, parameters);

			int successCount = experimentStep(graph, groupSize, treeFinder,
					finderName);

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
			ConstrainedSteinerTreeFinder treeFinder, String finderName) {

		int successCount = 0;
		Graph copy = graph.copy();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy)) {

			List<Node> group = nodeGroupper.group(copy, groupSize);
			List<Double> constraints = constraintsChooser.choose(copy, group);

			Tree tree = treeFinder.find(copy, group, constraints);

			if (tree == null) {
				failuresStore(graph, group, constraints, finderName);
				break;
			}

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
				nodeSize, setup.getGraphsInFile(), bufferedReader);

		return inputGraphStreamer;
	}

	private Map<String, ConstrainedSteinerTreeFinder> allocateFinders() {

		// Factories.
		// ----------
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Strategies.
		// -----------
		MetricProvider metricProvider = new IndexMetricProvider(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// MLARAC path finder.
		// -------------------
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();

		ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(
				pathSubstitutor, lambdaEstimator, constraintsComparer);

		// LBPSA path finder.
		// ------------------
		ConstrainedPathFinder lbpsa = pathFinderFactory
				.createLbpsa(constraintsComparer);

		// HMCOP path finder.
		// ------------------
		double lambda = Double.POSITIVE_INFINITY;
		ConstrainedPathFinder hmcop = pathFinderFactory.createHmcop(lambda);

		// Build the result.
		// -----------------
		Map<String, ConstrainedSteinerTreeFinder> treeFinders = new HashMap<>();

		treeFinders.put("HMCMC", treeFinderFactory.createHmcmc(
				constraintsComparer, pathFinderFactory, pathAggregator));

		treeFinders.put("AGGR_MLARAC", treeFinderFactory
				.createConstrainedPathAggr(mlarac, pathAggregator));

		treeFinders.put("AGGR_LBPSA", treeFinderFactory
				.createConstrainedPathAggr(lbpsa, pathAggregator));

		treeFinders.put("AGGR_HMCOP", treeFinderFactory
				.createConstrainedPathAggr(hmcop, pathAggregator));

		return treeFinders;
	}

	// Failure record related.

	private void failuresReset() {
		failedProblems = new ArrayList<>();
	}

	private void failuresStore(Graph graph, List<Node> group,
			List<Double> constraints, String finderName) {

		List<Integer> groupIds = new ArrayList<>();
		for (Node node : group) {
			groupIds.add(node.getId());
		}

		failedProblems.add(new ConstrainedTreeFindProblemDTO(GraphFactory
				.createDTO(graph), groupIds, constraints, finderName));
	}

	private void failuresReport() {

		// Make sure the target directory exists.
		File drainageFailDir = new File("drainagefail");
		if (!drainageFailDir.exists()) {
			drainageFailDir.mkdir();
		} else if (drainageFailDir.isFile()) {
			drainageFailDir.delete();
			drainageFailDir.mkdir();
		}

		// Clear the directory.
		for (File child : drainageFailDir.listFiles()) {
			child.delete();
		}

		// Write the failure reports.
		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();
		for (ConstrainedTreeFindProblemDTO problem : failedProblems) {
			String path = drainageFailDir.getPath() + "/problem"
					+ problem.hashCode() + ".xml";
			marshaller.writeToFile(path, problem);
		}
	}
}