package apps.alganal;

import helpers.ConstraintsComparer;
import helpers.OspfCostResourceTranslation;
import helpers.PathAggregator;
import helpers.TopologyAnalyser;
import helpers.gphmut.IndexResourceDrainer;
import helpers.gphmut.ResourceDrainer;
import helpers.metrprov.IndexMetricProvider;
import helpers.nodegrp.CentroidNodeGroupper;
import helpers.nodegrp.DegreeNodeGroupper;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import pfnd.mlarac.IntersectLambdaEstimator;
import pfnd.mlarac.MlaracPathFinder;
import tfind.ConstrainedSteinerTreeFinder;
import tfind.SpanningTreeFinder;
import tfind.hmcmc.HmcmcTreeFinder;
import tfind.paggr.ConstrainedPathAggrTreeFinder;
import tfind.prim.PrimTreeFinder;
import tfind.rdp.RdpQuasiExact;
import apps.TimeMeasurement;
import dal.TopologyDAO;
import dto.GraphDTO;

public class AlgorithmAnalysisRunnable implements Runnable {

	private final AlgorithmExperimentCase experimentCase;
	private final Connection connection;

	private static final double DRAINED_BANDWIDTH = 100.0;
	private static final double MIN_BANDWIDTH = 1.0;
	private static final double BASE_BANDWIDTH = 10000.0;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
	private final SpanningTreeFinder spanningTreeFinder = new PrimTreeFinder(
			new IndexMetricProvider(0));
	private final ResourceDrainer resourceDrainer = new IndexResourceDrainer(
			new OspfCostResourceTranslation(BASE_BANDWIDTH), 0, graphFactory);

	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager
			.getLogger(AlgorithmAnalysisRunnable.class);

	private static Random r = new Random(System.currentTimeMillis());

	public AlgorithmAnalysisRunnable(AlgorithmExperimentCase experimentCase,
			Connection connection) {
		this.experimentCase = experimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {
		logger.trace("Begin analysis for case {}", experimentCase);

		AlgorithmExperimentValues experimentValues = AlgorithmResultDataAccess
				.selectResultForCase(connection, experimentCase);

		if (experimentValues == null) {
			logger.trace("No result found, commencing...");
			AlgorithmResultDataAccess.insert(connection, experimentCase,
					new AlgorithmExperimentValues(new ArrayList<Double>(), -1));
		} else if (experimentValues.isValid()) {
			logger.trace("Valid result for case found, aborting...");
			return;
		} else {
			logger.trace("Found invalid result, resuming...");
		}

		Graph graph = getGraph();
		if (graph == null) {
			return;
		}

		ConstrainedSteinerTreeFinder treeFinder = getTreeFinder();
		if (treeFinder == null) {
			return;
		}

		NodeGroupper nodeGroupper = getNodeGroupper(graph);

		ArrayList<Double> constraints = new ArrayList<>();
		for (int i = 0; i < (graph.getNumMetrics() - 1); ++i) {
			constraints.add(experimentCase.getConstraintBase());
		}

		timeMeasurement.begin();
		experimentValues = compute(graph, treeFinder, nodeGroupper,
				experimentCase, constraints);
		timeMeasurement.end();

		logger.debug("Computed multicast group parameters for a graph in {}",
				timeMeasurement.getDurationString());

		AlgorithmResultDataAccess.update(connection, experimentCase,
				experimentValues);
	}

	private AlgorithmExperimentValues compute(Graph graph,
			ConstrainedSteinerTreeFinder treeFinder, NodeGroupper nodeGroupper,
			AlgorithmExperimentCase experimentCase, List<Double> constraints) {

		boolean isFirstPass = true;
		int G = experimentCase.getGroupSize();
		Graph copy = graph.copy();

		ArrayList<Double> firstCosts = new ArrayList<>();
		int successCount = 0;

		while (TopologyAnalyser.isConnected(copy, spanningTreeFinder)
				&& copy.getNodes().size() >= G) {

			List<Node> group = nodeGroupper.group(copy, G);

			Tree tree = treeFinder.find(copy, group, constraints);
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

			copy = resourceDrainer.drain(copy, tree, DRAINED_BANDWIDTH,
					MIN_BANDWIDTH);
		}

		return new AlgorithmExperimentValues(firstCosts, successCount);
	}

	private NodeGroupper getNodeGroupper(Graph graph) {
		switch (experimentCase.getNodeGroupperType()) {
		case Degree:
			return new DegreeNodeGroupper();

		case Centroid:
			Double minX = 0.0;
			Double maxX = 0.0;
			Double minY = 0.0;
			Double maxY = 0.0;
			TopologyAnalyser.minMaxCoordinates(graph, minX, maxX, minY, maxY);
			double cx = minX + (maxX - minX) * r.nextDouble();
			double cy = minY + (maxY - minY) * r.nextDouble();
			return new CentroidNodeGroupper(cx, cy);

		case Random:
			return new RandomNodeGroupper(r);

		default:
			return null;
		}
	}

	private ConstrainedSteinerTreeFinder getTreeFinder() {
		ConstrainedSteinerTreeFinder result = null;
		switch (experimentCase.getTreeFinderType()) {
		case AggrMLARAC:
			result = new ConstrainedPathAggrTreeFinder(new MlaracPathFinder(
					new ExpensiveNonBreakingPathSubstitutor(),
					new IntersectLambdaEstimator(), pathFinderFactory,
					new ConstraintsComparer()), new PathAggregator(
					new PrimTreeFinder(new IndexMetricProvider(0))));
			break;
		case HMCMC:
			result = new HmcmcTreeFinder(new ConstraintsComparer(),
					pathFinderFactory, new PathAggregator(new PrimTreeFinder(
							new IndexMetricProvider(0))));
			break;
		case RDP:
			result = new RdpQuasiExact(new ConstraintsComparer());
			break;
		}

		return result;
	}

	private Graph getGraph() {
		TopologyDAO topologyDAO = new TopologyDAO(connection);
		GraphDTO graphDTO;
		try {
			graphDTO = topologyDAO.select(experimentCase.getTopologyType(),
					experimentCase.getNodesCount(),
					experimentCase.getGraphIndex());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
		return graphFactory.createFromDTO(graphDTO);
	}

}
