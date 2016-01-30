package apps.algorthoanal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.TimeMeasurement;
import dal.TopologyDAO;
import dto.GraphDTO;
import dto.GroupDTO;
import dto.SubgraphDTO;
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
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;
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

public class AlgorithmOrthogonalAnalysisRunnable implements Runnable {

	private final AlgorithmOrthogonalExperimentCase experimentCase;
	private final Connection connection;

	private static final double DRAINED_BANDWIDTH = 100.0;
	private static final double MIN_BANDWIDTH = 1.0;
	private static final double BASE_BANDWIDTH = 10000.0;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
	private final SpanningTreeFinder spanningTreeFinder = new PrimTreeFinder(new IndexMetricProvider(0));
	private final ResourceDrainer resourceDrainer = new IndexResourceDrainer(
			new OspfCostResourceTranslation(BASE_BANDWIDTH), 0, graphFactory);

	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager.getLogger(AlgorithmOrthogonalAnalysisRunnable.class);

	private static Random r = new Random(System.currentTimeMillis());

	public AlgorithmOrthogonalAnalysisRunnable(AlgorithmOrthogonalExperimentCase experimentCase,
			Connection connection) {
		this.experimentCase = experimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {

		try {

			logger.trace("Begin analysis for case {}", experimentCase);

			AlgorithmOrthogonalAnalysisDataAccess.synchronize(connection, experimentCase);

			if (experimentCase.isPerformed()) {
				logger.trace("Experiment already performed, aborting...");
				return;
			}

			AlgorithmOrthogonalAnalysisDataAccess.clearResults(connection, experimentCase); // remove
																							// remains
																							// of
																							// unfinished
																							// experiment

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
			constraints.add(experimentCase.getConstraint1());
			constraints.add(experimentCase.getConstraint2());

			List<AlgorithmOrthogonalExperimentResult> experimentResults = compute(experimentCase, graph, treeFinder,
					nodeGroupper, constraints);

			AlgorithmOrthogonalAnalysisDataAccess.putResults(connection, experimentCase, experimentResults);

		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Runnable error: {}", e.getMessage());
		}
	}

	private List<AlgorithmOrthogonalExperimentResult> compute(AlgorithmOrthogonalExperimentCase experimentCase,
			Graph graph, ConstrainedSteinerTreeFinder treeFinder, NodeGroupper nodeGroupper, List<Double> constraints) {

		int G = experimentCase.getGroupSize();
		Graph copy = graph.copy();
		List<AlgorithmOrthogonalExperimentResult> results = new ArrayList<>();

		while (TopologyAnalyser.isConnected(copy, spanningTreeFinder) && copy.getNodes().size() >= G) {

			List<Node> group = nodeGroupper.group(copy, G);

			timeMeasurement.begin();
			Tree tree = treeFinder.find(copy, group, constraints);
			timeMeasurement.end();

			if (tree == null) {
				break;
			}

			GroupDTO groupDTO = GroupDTO.fromNodeList(group);
			SubgraphDTO treeDTO = SubgraphDTO.fromSubgraph(tree);

			double seconds = (double) timeMeasurement.getNanos() / 1000000000.0;
			results.add(
					new AlgorithmOrthogonalExperimentResult(
							experimentCase.getId(), treeDTO, groupDTO, seconds));

			copy = resourceDrainer.drain(copy, tree, DRAINED_BANDWIDTH, MIN_BANDWIDTH);
		}

		return results;
	}

	private NodeGroupper getNodeGroupper(Graph graph) {
		switch (experimentCase.getNodeGroupperType()) {
		case Degree:
			return new DegreeNodeGroupper();

		case Centroid02:
			return new CentroidNodeGroupper(0.2);

		case Centroid06:
			return new CentroidNodeGroupper(0.6);

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
			result = new ConstrainedPathAggrTreeFinder(
					new MlaracPathFinder(new ExpensiveNonBreakingPathSubstitutor(), new IntersectLambdaEstimator(),
							pathFinderFactory, new ConstraintsComparer()),
					new PathAggregator(new PrimTreeFinder(new IndexMetricProvider(0))));
			break;
		case HMCMC:
			result = new HmcmcTreeFinder(new ConstraintsComparer(), pathFinderFactory,
					new PathAggregator(new PrimTreeFinder(new IndexMetricProvider(0))));
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
			graphDTO = topologyDAO.select(experimentCase.getTopologyType(), experimentCase.getNodesCount(),
					experimentCase.getGraphIndex());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
		return graphFactory.createFromDTO(graphDTO);
	}

}
