package apps.legacy;

import helpers.ConstraintsComparer;
import helpers.ConstraintsComparerImpl;
import helpers.CostResourceTranslation;
import helpers.OspfCostResourceTranslation;
import helpers.PathAggregator;
import helpers.PathAggregatorImpl;
import helpers.TopologyAnalyser;
import helpers.TopologyAnalyserImpl;
import helpers.gphmut.IndexResourceDrainer;
import helpers.gphmut.ResourceDrainer;
import helpers.gphmut.UniformDistributionParameters;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

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

import model.topology.AdjacencyListFactory;
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
import dal.DTOMarshaller;
import dal.InputGraphStreamer;
import dal.MPiechGraphStreamer;
import dto.AdHocProblemDTO;
import dto.ConstrainedTreeFindProblemDTO;
import dto.GraphDTO;

public class MultiCostDrainLogic {

	// General utilities.
	private final Random random;

	// Factories.
	private final GraphFactory graphFactory;
	private final TreeFinderFactory treeFinderFactory;

	// Strategies.
	private final NodeGroupper nodeGroupper;
	private final CostResourceTranslation costResourceTranslation;
	private final ResourceDrainer resourceDrainer;
	private final MetricProvider metricProvider;

	// Finders.
	private final SpanningTreeFinder spanningTreeFinder;
	private final Map<String, ConstrainedSteinerTreeFinder> treeFinders;

	// Graph streams.
	private final List<String> streamerNames;

	// Special utilities.
	private final TopologyAnalyser topologyAnalyser;

	// Procedure setup.
	private MultiCostDrainSetup setup;

	public MultiCostDrainLogic(MultiCostDrainSetup setup) {

		random = new Random(setup.getRandomSeed());
		graphFactory = new AdjacencyListFactory();
		treeFinderFactory = new TreeFinderFactoryImpl();

		nodeGroupper = new RandomNodeGroupper(random);

		costResourceTranslation = new OspfCostResourceTranslation(
				setup.getBaseBandwidth());

		resourceDrainer = new IndexResourceDrainer(costResourceTranslation,
				setup.getDrainedIndex(), graphFactory);

		metricProvider = new IndexMetricProvider(0);

		spanningTreeFinder = treeFinderFactory.createPrim(metricProvider);
		treeFinders = allocateFinders();

		streamerNames = new ArrayList<>();

		topologyAnalyser = new TopologyAnalyserImpl();

		this.setup = setup;
	}

	public void run(String[] args, OutputStream out, OutputStream debug) {

		failuresReset();
		TimeMeasurement timeMeasurement = new TimeMeasurement();
		StringBuilder result = new StringBuilder();
		PrintWriter debugWriter = new PrintWriter(debug, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");

		for (Integer criteriaCount : setup.getCriteriaCounts()) {
			String critString = "crit = " + criteriaCount;

			for (Integer groupSize : setup.getGroupSizes()) {
				String critGroupString = critString + " g = " + groupSize;

				for (List<Double> constraints : setup.GetConstraintCases()) {
					String critGroupConstrString = critGroupString + " cstr = "
							+ toString(constraints, ",");

					for (String finderName : setup.getTreeFinderNames()) {
						String critGroupConstrFndString = critGroupConstrString
								+ " alg = " + finderName;

						streamerNamesReset();
						while (!streamerNames.isEmpty()) {
							String topName = streamerNames.get(streamerNames
									.size() - 1);
							String problemString = sdf.format(new Date()) + " "
									+ critGroupConstrFndString + " top = "
									+ topName;

							debugWriter.print(problemString);
							debugWriter.flush();

							timeMeasurement.begin();

							// Cartesian product of case variables.
							String partialResult = experiment(topName,
									criteriaCount, groupSize,
									setup.getGraphs(), constraints, finderName,
									treeFinders.get(finderName));

							timeMeasurement.end();

							debugWriter.println(" Elapsed : "
									+ timeMeasurement.getDurationString());

							if (partialResult == null) {
								debugWriter.println("Experiment failed.");
								return;
							}

							result.append(partialResult);

							streamerNames.remove(streamerNames.size() - 1);
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

	private void streamerNamesReset() {
		streamerNames.clear();
		streamerNames.add("n_100_DistRNG_r_250__N_200");
		streamerNames.add("n_100_k_100_Waxman_015_005__N_200");
		streamerNames.add("n_100_k_200_Waxman_015_005__N_200");
		streamerNames.add("n_100_LMST_r_250__N_200");
	}

	private String experiment(String topName, Integer criteriaCount,
			Integer groupSize, int graphs, List<Double> constraints,
			String finderName, ConstrainedSteinerTreeFinder treeFinder) {

		final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(topName);
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

			CostDrainResult result = experimentStep(graph, groupSize,
					constraints, treeFinder, finderName);

			resultStringBuilder.append(topName);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(finderName);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(criteriaCount);
			resultStringBuilder.append('\t');
			
			resultStringBuilder.append(toString(constraints, ","));
			resultStringBuilder.append('\t');

			resultStringBuilder.append(groupSize);
			resultStringBuilder.append('\t');

			resultStringBuilder.append(result.getSuccessCount());
			resultStringBuilder.append('\t');

			List<Double> firstCosts = result.getFirstCosts();
			for (int i = 0; i < firstCosts.size(); ++i) {
				resultStringBuilder.append(firstCosts.get(i));
				if (i < (firstCosts.size() - 1))
					resultStringBuilder.append('\t');
			}

			resultStringBuilder.append('\n');
		}

		return resultStringBuilder.toString();
	}

	private CostDrainResult experimentStep(Graph graph, int groupSize,
			List<Double> constraints, ConstrainedSteinerTreeFinder treeFinder,
			String finderName) {

		boolean isFirstPass = true;
		Graph copy = graph.copy();
		int successCount = 0;
		List<Double> firstCosts = new ArrayList<>();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy, spanningTreeFinder)
				&& copy.getNodes().size() >= groupSize) {

			List<Node> group = nodeGroupper.group(copy, groupSize);

			debugDumpCreate(graph, group, finderName);

			Tree tree = treeFinder.find(copy, group, constraints);

			if (tree == null) {
				failuresStore(graph, group, constraints, finderName);
				break;
			}

			debugDumpDestroy();

			// Optionally record the first results.
			if (isFirstPass) {
				for (Double m : tree.getMetrics()) {
					firstCosts.add(m);
				}
				isFirstPass = false;
			}

			// Unconditionally record the success.
			++successCount;

			copy = resourceDrainer.drain(copy, tree,
					setup.getDrainedBandwidth(), setup.getMinBandwidth());
		}

		return new CostDrainResult(successCount, firstCosts);
	}

	private InputGraphStreamer prepareGraphStreamer(String topName) {

		String edgesFilename = setup.getTopologiesDirectory() + "/edges_"
				+ topName + ".txt";
		String nodesFilename = setup.getTopologiesDirectory() + "/nodes_"
				+ topName + ".txt";

		BufferedReader edgesReader = null;
		BufferedReader nodesReader = null;

		try {
			edgesReader = new BufferedReader(new FileReader(edgesFilename));
			nodesReader = new BufferedReader(new FileReader(nodesFilename));
		} catch (FileNotFoundException exception) {
			return null;
		}

		InputGraphStreamer inputGraphStreamer = new MPiechGraphStreamer(200,
				nodesReader, edgesReader);

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

		treeFinders.put("RDP_QE",
				treeFinderFactory.createRdpQuasiExact(constraintsComparer));

		treeFinders.put("RDP_H",
				treeFinderFactory.createRdpHeuristic(constraintsComparer));

		return treeFinders;
	}

	// Failure record related.

	// TODO: Abstract this mechanism.
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

	// TODO: Abstract this mechanism too.
	private void debugDumpCreate(Graph graph, List<Node> group,
			String finderName) {

		List<Integer> groupIds = new ArrayList<>();
		for (Node node : group) {
			groupIds.add(node.getId());
		}

		GraphDTO graphDto = GraphFactory.createDTO(graph);

		AdHocProblemDTO problem = new AdHocProblemDTO(graphDto, groupIds,
				finderName);
		
		DTOMarshaller<AdHocProblemDTO> marshaller = new DTOMarshaller<>();
		
		File debugDataFile = new File("debug_data/current_problem.xml");
		String path = debugDataFile.getPath();
		marshaller.writeToFile(path, problem);
	}

	private void debugDumpDestroy() {
		File debugDataFile = new File("debug_data/current_problem.xml");
		if (debugDataFile.exists()) {
			debugDataFile.delete();
		}
	}

	private String toString(List<Double> values, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.size(); ++i) {
			sb.append(values.get(i));
			if (i < (values.size() - 1)) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
