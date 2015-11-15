package apps.groupanal;

import helpers.PathMetric;
import helpers.TopologyAnalyser;
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

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.TimeMeasurement;
import dal.TopologyDAO;
import dto.GraphDTO;

public class GroupAnalysisRunnable implements Runnable {

	private final GroupExperimentCase experimentCase;
	private final Connection connection;

	private final int neededGroupResults = 30;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager.getLogger(GroupAnalysisRunnable.class);

	private static Random r = new Random(System.currentTimeMillis());

	public GroupAnalysisRunnable(GroupExperimentCase experimentCase, Connection connection) {
		this.experimentCase = experimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {

		logger.trace("Begin analysis for case {}", experimentCase);

		GroupExperimentValues experimentValues = GroupResultDataAccess.selectResultForCase(connection, experimentCase);

		if (experimentValues == null) {
			logger.trace("No result found, commencing...");
			GroupResultDataAccess.insert(connection, experimentCase, new GroupExperimentValues(-1, -1, -1, -1, -1));
		} else if (experimentValues.isValid()) {
			logger.trace("Valid result for case found, aborting...");
			return;
		} else {
			logger.trace("Found invalid result, resuming...");
		}

		TopologyDAO topologyDAO = new TopologyDAO(connection);
		GraphDTO graphDTO;
		try {
			graphDTO = topologyDAO.select(experimentCase.getTopologyType(), experimentCase.getNodesCount(),
					experimentCase.getGraphIndex());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return;
		}
		Graph graph = graphFactory.createFromDTO(graphDTO);

		timeMeasurement.begin();

		switch (experimentCase.getNodeGroupperType()) {
		case Degree:
			experimentValues = compute(graph, new DegreeNodeGroupper());
			break;

		case Centroid:
			experimentValues = compute(graph, genCentroidGrouppers(graph, neededGroupResults));
			break;

		case Random:
			experimentValues = compute(graph, genRandomGrouppers(neededGroupResults));
			break;
		}

		timeMeasurement.end();
		logger.debug("Computed multicast group parameters for a graph in {}", timeMeasurement.getDurationString());

		GroupResultDataAccess.update(connection, experimentCase, experimentValues);
	}

	private GroupExperimentValues compute(Graph graph, NodeGroupper nodeGroupper) {

		List<Node> group = nodeGroupper.group(graph, experimentCase.getGroupSize());

		double degree = TopologyAnalyser.averageDegree(graph, group);
		double clusteringCoefficient = TopologyAnalyser.clusteringCoefficient(graph, group);
		double density = TopologyAnalyser.nodeGroupoDensity(graph, group);

		// TODO: Limit this once estimated upper limit of reason.
		PathMetric diameter = TopologyAnalyser.diameter(graph, group);

		return new GroupExperimentValues(degree, diameter.getHop(), diameter.getCost(), clusteringCoefficient, density);
	}

	private GroupExperimentValues compute(Graph graph, ArrayList<NodeGroupper> grouppers) {

		SummaryStatistics degreeStat = new SummaryStatistics();
		SummaryStatistics clusteringCoefficientStat = new SummaryStatistics();
		SummaryStatistics densityStat = new SummaryStatistics();
		SummaryStatistics diameterHopStat = new SummaryStatistics();
		SummaryStatistics diameterCostStat = new SummaryStatistics();

		for (NodeGroupper groupper : grouppers) {
			GroupExperimentValues partialValues = compute(graph, groupper);
			degreeStat.addValue(partialValues.getDegree());
			diameterHopStat.addValue(partialValues.getDiameterHop());
			diameterCostStat.addValue(partialValues.getDiameterCost());
			clusteringCoefficientStat.addValue(partialValues.getClusteringCoefficient());
			densityStat.addValue(partialValues.getClusteringCoefficient());
		}

		return new GroupExperimentValues(degreeStat.getMean(), diameterHopStat.getMean(), diameterCostStat.getMean(),
				clusteringCoefficientStat.getMean(), densityStat.getMean());
	}

	private ArrayList<NodeGroupper> genRandomGrouppers(int count) {
		ArrayList<NodeGroupper> result = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			result.add(new RandomNodeGroupper(r));
		}
		return result;
	}

	private ArrayList<NodeGroupper> genCentroidGrouppers(Graph graph, int count) {

		Double minX = 0.0, maxX = 0.0, minY = 0.0, maxY = 0.0;
		TopologyAnalyser.minMaxCoordinates(graph, minX, maxX, minY, maxY);

		ArrayList<NodeGroupper> result = new ArrayList<>();
		for (int i = 0; i < count; ++i) {
			double cx = minX + (maxX - minX) * r.nextDouble();
			double cy = minY + (maxY - minY) * r.nextDouble();
			result.add(new CentroidNodeGroupper(cx, cy));
		}

		return result;
	}

}
