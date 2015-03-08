package apps.phd;

import helpers.ConstraintsComparer;
import helpers.ConstraintsComparerImpl;
import helpers.OspfCostResourceTranslation;
import helpers.PathAggregator;
import helpers.PathAggregatorImpl;
import helpers.TopologyAnalyser;
import helpers.TopologyAnalyserImpl;
import helpers.gphmut.IndexResourceDrainer;
import helpers.gphmut.ResourceDrainer;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dal.DTOMarshaller;
import dto.ConstrainedTreeFindProblemDTO;
import dto.GraphDTO;
import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import pfnd.mlarac.IntersectLambdaEstimator;
import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.PathSubstiutor;
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;
import tfind.ConstrainedSteinerTreeFinder;
import tfind.SpanningTreeFinder;
import tfind.TreeFinderFactory;
import tfind.TreeFinderFactoryImpl;

public class MultiCostDrainExecutor {

	// Factories.
	private static final GraphFactory graphFactory = new AdjacencyListFactory();
	private static final TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();
	private static final TopologyAnalyser topologyAnalyser = new TopologyAnalyserImpl();
	private static final Map<String, ConstrainedSteinerTreeFinder> treeFinders = allocateFinders();
	private static final SpanningTreeFinder spanningTreeFinder = treeFinderFactory
			.createPrim(new IndexMetricProvider(0));

	private final ResourceDrainer resourceDrainer;
	private final NodeGroupper nodeGroupper;

	private final double drainedBandwidth;
	private final double minBandwidth;

	public MultiCostDrainExecutor(MultiCostDrainSetup setup, Random random) {
		resourceDrainer = new IndexResourceDrainer(
				new OspfCostResourceTranslation(setup.getBaseBandwidth()),
				setup.getDrainedIndex(), graphFactory);
		nodeGroupper = new RandomNodeGroupper(random);
		drainedBandwidth = setup.getDrainedBandwidth();
		minBandwidth = setup.getMinBandwidth();
	}

	public CostDrainResult execute(Graph graph, int groupSize,
			List<Double> constraints, String finderName) {

		boolean isFirstPass = true;
		Graph copy = graph.copy();
		int successCount = 0;
		List<Double> firstCosts = new ArrayList<>();

		ConstrainedSteinerTreeFinder treeFinder = treeFinders.get(finderName);

		// Drainage loop
		while (topologyAnalyser.isConnected(copy, spanningTreeFinder)
				&& copy.getNodes().size() >= groupSize) {

			List<Node> group = nodeGroupper.group(copy, groupSize);

			debugDumpCreate(graph, group, constraints, finderName);
			Tree tree = treeFinder.find(copy, group, constraints);
			debugDumpDestroy();
			
			if (tree == null) {
				break;
			}

			if (isFirstPass) {
				for (Double m : tree.getMetrics()) {
					firstCosts.add(m);
				}
				isFirstPass = false;
			}

			++successCount;

			copy = resourceDrainer.drain(copy, tree, drainedBandwidth,
					minBandwidth);
		}

		return new CostDrainResult(successCount, firstCosts);
	}

	private static Map<String, ConstrainedSteinerTreeFinder> allocateFinders() {

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
	
	private void debugDumpCreate(Graph graph, List<Node> group,
			List<Double> constraints, String finderName) {

		List<Integer> groupIds = new ArrayList<>();
		for (Node node : group) {
			groupIds.add(node.getId());
		}

		GraphDTO graphDto = GraphFactory.createDTO(graph);

		ConstrainedTreeFindProblemDTO problem = new ConstrainedTreeFindProblemDTO(
				graphDto, groupIds, constraints, finderName);

		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();

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
}
