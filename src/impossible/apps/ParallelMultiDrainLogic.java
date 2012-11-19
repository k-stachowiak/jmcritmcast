package impossible.apps;

import impossible.dal.DTOMarshaller;
import impossible.dal.InputGraphStreamer;
import impossible.dal.NewFormatGraphStreamer;
import impossible.dto.ConstrainedTreeFindProblemDTO;
import impossible.dto.GraphDTO;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.CostResourceTranslation;
import impossible.helpers.OspfCostResourceTranslation;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.TopologyAnalyser;
import impossible.helpers.TopologyAnalyserImpl;
import impossible.helpers.cstrch.FengGroupConstraintsChooser;
import impossible.helpers.cstrch.GroupConstraintsChooser;
import impossible.helpers.gphmut.MetricRedistribution;
import impossible.helpers.gphmut.MetricRedistributionImpl;
import impossible.helpers.gphmut.IndexResourceDrainer;
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
import impossible.tfind.MetricConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;
import impossible.util.TimeMeasurement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMultiDrainLogic {
	
	// Configuration.
	private final int numThreads;

	// General utilities.
	private final Random random;

	// Factories.
	private final GraphFactory graphFactory;
	private final PathFinderFactory pathFinderFactory;
	private final TreeFinderFactory treeFinderFactory;

	// Strategies.
	private final GroupConstraintsChooser constraintsChooser;
	private final NodeGroupper nodeGroupper;
	private final CostResourceTranslation costResourceTranslation;
	private final ResourceDrainer resourceDrainer;
	private final MetricProvider metricProvider;
	private final MetricRedistribution metricResistribution;

	// Finders.
	private final SpanningTreeFinder spanningTreeFinder;
	private final Map<String, MetricConstrainedSteinerTreeFinder> treeFinders;

	// Special utilities.
	private final TopologyAnalyser topologyAnalyser;

	// Procedure setup.
	private MultiDrainSetup setup;

	public ParallelMultiDrainLogic(int numThreads, MultiDrainSetup setup) {
		
		this.numThreads = numThreads;

		random = new Random(setup.getRandomSeed());
		graphFactory = new AdjacencyListFactory();
		pathFinderFactory = new PathFinderFactoryImpl();
		treeFinderFactory = new TreeFinderFactoryImpl();

		constraintsChooser = new FengGroupConstraintsChooser(
				setup.getFengDelta(), pathFinderFactory);

		nodeGroupper = new RandomNodeGroupper(random);

		costResourceTranslation = new OspfCostResourceTranslation(
				setup.getBaseBandwidth());

		resourceDrainer = new IndexResourceDrainer(costResourceTranslation,
				setup.getDrainedIndex(), graphFactory);

		metricProvider = new IndexMetricProvider(0);

		metricResistribution = new MetricRedistributionImpl(graphFactory,
				random);

		spanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		treeFinders = allocateFinders();

		topologyAnalyser = new TopologyAnalyserImpl(spanningTreeFinder);

		this.setup = setup;
	}

	// Computations.
	// =============

	public void run(String[] args, OutputStream out, OutputStream debug) {

		failuresReset();
		StringBuilder result = new StringBuilder();
		PrintWriter debugWriter = new PrintWriter(debug, true);

		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

		experimentsCarthesianProduct(executorService, result, debugWriter);
		executorService.shutdown();

		try {
			executorService.awaitTermination(7, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			debugWriter.println("Executor interrupted.");
			return;
		}

		PrintWriter outWriter = new PrintWriter(out, true);
		outWriter.print(result.toString());
		outWriter.close();

		debugWriter.println("Terminated normally");

		debugWriter.close();
	}

	private void experimentsCarthesianProduct(ExecutorService executorService,
			final StringBuilder result, final PrintWriter debugWriter) {

		// Cartesian product of case variables.
		for (final Integer nodeSize : setup.getNodeSizes()) {
			for (final Integer criteriaCount : setup.getCriteriaCounts()) {
				for (final Integer groupSize : setup.getGroupSizes()) {
					for (final String finderName : setup.getTreeFinderNames()) {

						executorService.submit(new Runnable() {
							@Override
							public void run() {

								String partialResult = experiment(nodeSize,
										criteriaCount, groupSize,
										setup.getGraphs(), finderName,
										treeFinders.get(finderName),
										debugWriter);

								result.append(partialResult);
							}
						});
					}
				}
			}
		}
	}

	private String experiment(Integer nodeSize, Integer criteriaCount,
			Integer groupSize, int graphs, String finderName,
			MetricConstrainedSteinerTreeFinder treeFinder, PrintWriter debugWriter) {

		// Result builder.
		StringBuilder resultStringBuilder = new StringBuilder();

		// Helpers.
		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");
		final TimeMeasurement timeMeasurement = new TimeMeasurement();

		final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(nodeSize);
		if (inputGraphStreamer == null) {
			throw new RuntimeException("Failed opening graph streamer.\n");
		}

		// Setup the metrics redistribution.
		List<UniformDistributionParameters> parameters = new ArrayList<>();
		for (int p = 0; p < criteriaCount; ++p)
			parameters.add(new UniformDistributionParameters(setup
					.getRedistributionMin(), setup.getRedistributionMax()));

		// Begin time measure.
		timeMeasurement.begin();

		// Loop through requested number of graphs.
		for (int g = 0; g < graphs; ++g) {

			GraphDTO graphDTO = inputGraphStreamer.getNext();
			Graph graph = graphFactory.createFromDTO(graphDTO);
			graphDTO = null;

			graph = metricResistribution.redistUniform(graph, parameters);

			int successCount = experimentStep(graph, groupSize, treeFinder,
					finderName);

			appendResult(resultStringBuilder, nodeSize, finderName,
					criteriaCount, groupSize, successCount);
		}

		// End time measure.
		timeMeasurement.end();

		// Report some debug information.
		String problemString = String.format(
				"%s n = %d c = %d g = %d alg = %s", sdf.format(new Date()),
				nodeSize, criteriaCount, groupSize, finderName);

		debugWriter.println(problemString + " Elapsed : "
				+ timeMeasurement.getDurationString());

		return resultStringBuilder.toString();
	}

	private int experimentStep(Graph graph, int groupSize,
			MetricConstrainedSteinerTreeFinder treeFinder, String finderName) {

		int successCount = 0;
		Graph copy = graph.copy();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy)
				&& copy.getNodes().size() >= groupSize) {

			List<Node> group = nodeGroupper.group(copy, groupSize);
			List<Double> constraints = constraintsChooser.choose(copy, group);

			Tree tree = treeFinder.find(copy, group, constraints);

			if (tree == null) {
				failuresStore(graph, group, constraints, finderName);
				break;
			}

			++successCount;
			copy = resourceDrainer.drain(copy, tree,
					setup.getDrainedBandwidth(), setup.getMinBandwidth());
		}

		return successCount;

	}

	private void appendResult(StringBuilder resultStringBuilder,
			Integer nodeSize, String finderName, Integer criteriaCount,
			Integer groupSize, int successCount) {

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

	// Resources allocation.
	// =====================

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

	private Map<String, MetricConstrainedSteinerTreeFinder> allocateFinders() {

		// Factories.
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Strategies.
		MetricProvider metricProvider = new IndexMetricProvider(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// MLARAC path finder.
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();

		ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(
				pathSubstitutor, lambdaEstimator, constraintsComparer);

		// LBPSA path finder.
		ConstrainedPathFinder lbpsa = pathFinderFactory
				.createLbpsa(constraintsComparer);

		// HMCOP path finder.
		double lambda = Double.POSITIVE_INFINITY;
		ConstrainedPathFinder hmcop = pathFinderFactory.createHmcop(lambda);

		// Build the result.
		Map<String, MetricConstrainedSteinerTreeFinder> treeFinders = new HashMap<>();

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
	// =======================

	private void failuresReset() {
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
	}

	private void failuresStore(Graph graph, List<Node> group,
			List<Double> constraints, String finderName) {

		List<Integer> groupIds = new ArrayList<>();
		for (Node node : group) {
			groupIds.add(node.getId());
		}

		ConstrainedTreeFindProblemDTO problem = new ConstrainedTreeFindProblemDTO(
				GraphFactory.createDTO(graph), groupIds, constraints,
				finderName);

		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();

		File drainageFailDir = new File("drainagefail");
		String path = drainageFailDir.getPath() + "/" + problem.getFinderName()
				+ "." + problem.getGraph().getNodes().size() + "."
				+ problem.hashCode() + ".xml";
		marshaller.writeToFile(path, problem);
	}
}
