package apps;


import helpers.ConstraintsComparer;
import helpers.ConstraintsComparerImpl;
import helpers.PathAggregator;
import helpers.PathAggregatorImpl;
import helpers.cstrch.FengGroupConstraintsChooser;
import helpers.cstrch.GroupConstraintsChooser;
import helpers.gphmut.MetricRedistribution;
import helpers.gphmut.MetricRedistributionImpl;
import helpers.gphmut.UniformDistributionParameters;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.io.BufferedReader;
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

import model.topology.AdjacencyListFactory;
import model.topology.AdjacencyMatrixFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import pfnd.mlarac.IntersectLambdaEstimator;
import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.PathSubstiutor;

import tfind.ConstrainedSteinerTreeFinder;
import tfind.SpanningTreeFinder;
import tfind.TreeFinderFactory;
import tfind.TreeFinderFactoryImpl;
import util.TimeMeasurement;

import dal.InputGraphStreamer;
import dal.NewFormatGraphStreamer;
import dto.GraphDTO;


public class TimeCostLogic {

	// General utilities.
	private final Random random;

	// Factories.
	private final PathFinderFactory pathFinderFactory;

	// Strategies.
	private final GroupConstraintsChooser constraintsChooser;
	private final NodeGroupper nodeGroupper;

	private final Map<String, GraphFactory> graphFactories;

	// Finders.
	private final Map<String, ConstrainedSteinerTreeFinder> treeFinders;

	// Procedure setup.
	private TimeCostSetup setup;

	public TimeCostLogic(TimeCostSetup setup) {

		random = new Random(setup.getRandomSeed());
		pathFinderFactory = new PathFinderFactoryImpl();

		constraintsChooser = new FengGroupConstraintsChooser(
				setup.getFengDelta(), pathFinderFactory);

		nodeGroupper = new RandomNodeGroupper(random);

		graphFactories = allocateGraphFactories();

		treeFinders = allocateFinders();

		this.setup = setup;
	}

	public void run(String[] args, OutputStream out, OutputStream debug) {

		TimeMeasurement timeMeasurement = new TimeMeasurement();
		StringBuilder result = new StringBuilder();
		PrintWriter debugWriter = new PrintWriter(debug, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");

		// Cartesian product of case variables.
		for (Integer nodeSize : setup.getNodeSizes()) {
			String nodesString = "n = " + nodeSize;
			for (Integer criteriaCount : setup.getCriteriaCounts()) {
				String nodeCritString = nodesString + " c = " + criteriaCount;
				for (Integer groupSize : setup.getGroupSizes()) {
					String nodeCritGroupString = nodeCritString + " g = "
							+ groupSize;
					for (String factoryName : setup
							.getTopologyImplementations()) {
						String nodeCritGroupGFacString = nodeCritGroupString + " gf = " + factoryName;
						for (String finderName : setup.getTreeFinderNames()) {

							String problemString = sdf.format(new Date()) + " "
									+ nodeCritGroupGFacString + " alg = "
									+ finderName;

							debugWriter.print(problemString);
							debugWriter.flush();
							
							timeMeasurement.begin();

							String partialResult = experiment(nodeSize,
									criteriaCount, groupSize,
									setup.getGraphs(), factoryName, finderName,
									treeFinders.get(finderName));
							
							timeMeasurement.end();
							debugWriter.println(" Elapsed : "
									+ timeMeasurement.getDurationString());

							if (partialResult == null) {
								debugWriter.println("Experiment failed.");
								return;
							}

							result.append(partialResult);
						}
					}
				}
			}
		}

		PrintWriter outWriter = new PrintWriter(out, true);
		outWriter.print(result.toString());
		outWriter.close();

		debugWriter.println("Terminated normally");

		debugWriter.close();
	}

	private String experiment(Integer nodeSize, Integer criteriaCount,
			Integer groupSize, int graphs, String factoryName,
			String finderName, ConstrainedSteinerTreeFinder treeFinder) {

		TimeMeasurement timeMeasurement = new TimeMeasurement();
		
		GraphFactory graphFactory = graphFactories.get(factoryName);
		MetricRedistribution metricResistribution = new MetricRedistributionImpl(graphFactory,
				random);

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

			// Fetch the data.
			GraphDTO graphDTO = inputGraphStreamer.getNext();
			Graph graph = graphFactory.createFromDTO(graphDTO);
			graphDTO = null;

			// Prepare the data and the utilities.
			graph = metricResistribution.redistUniform(graph, parameters);
			List<Node> group = nodeGroupper.group(graph, groupSize);
			List<Double> constraints = constraintsChooser.choose(graph, group);

			// Simulate routing.
			timeMeasurement.begin();
			Tree tree = treeFinder.find(graph, group, constraints);
			timeMeasurement.end();

			if (tree == null) {
				continue;
			}

			// Print the labels.
			resultStringBuilder.append(nodeSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(finderName);
			resultStringBuilder.append('\t');
			
			resultStringBuilder.append(factoryName);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(criteriaCount);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(groupSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append((double)timeMeasurement.getNanos() * 0.e-6);
			resultStringBuilder.append('\t');

			// Print the metrics.
			List<Double> metrics = tree.getMetrics();
			for (int i = 0; i < metrics.size(); ++i) {
				resultStringBuilder.append(metrics.get(i));
				if (i < (metrics.size() - 1))
					resultStringBuilder.append('\t');
			}

			resultStringBuilder.append('\n');
		}

		return resultStringBuilder.toString();
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

	private Map<String, GraphFactory> allocateGraphFactories() {
		Map<String, GraphFactory> graphFactories = new HashMap<>();
		graphFactories.put("AdjacencyMatrix", new AdjacencyMatrixFactory());
		graphFactories.put("AdjacencyList", new AdjacencyListFactory());
		return graphFactories;
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

		treeFinders.put("RDP_QE",
				treeFinderFactory.createRdpQuasiExact(constraintsComparer));

		treeFinders.put("RDP_H",
				treeFinderFactory.createRdpHeuristic(constraintsComparer));

		return treeFinders;
	}
}
