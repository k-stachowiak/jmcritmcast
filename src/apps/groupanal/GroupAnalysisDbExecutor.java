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

public class GroupAnalysisDbExecutor implements Runnable {

	private final GroupExperimentCase experimentCase;
	private final Connection connection;

	private final int neededGraphResults = 30;
	private final int neededGroupResults = 30;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager
			.getLogger(GroupAnalysisDbExecutor.class);

	private static Random r = new Random(System.currentTimeMillis());

	public GroupAnalysisDbExecutor(GroupExperimentCase experimentCase,
			Connection connection) {
		this.experimentCase = experimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {

		for (int graphIndex = 1; graphIndex <= neededGraphResults; ++graphIndex) {

			GroupExperiment experiment = GroupResultDataAccess
					.selectResultsForCaseAndGraphIndex(connection,
							experimentCase, graphIndex);

			if (experiment != null) {
				continue;
			}

			TopologyDAO topologyDAO = new TopologyDAO(connection);

			GroupResultDataAccess.insert(connection, new GroupExperiment(
					experimentCase, new GroupExperimentValues(graphIndex, -1,
							-1, -1, -1, -1)));

			GraphDTO graphDTO;
			try {
				graphDTO = topologyDAO.select(experimentCase.getTopologyType(),
						experimentCase.getNodesCount(), graphIndex);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.fatal("Sql error: {}", e.getMessage());
				continue;
			}

			Graph graph = graphFactory.createFromDTO(graphDTO);

			timeMeasurement.begin();

			GroupExperimentValues xv = null;

			switch (experimentCase.getNodeGroupperType()) {
			case Degree:
				xv = compute(graphIndex, graph, new DegreeNodeGroupper(),
						experimentCase);
				break;

			case Centroid:
				xv = compute(graphIndex, graph,
						genCentroidGrouppers(graph, neededGroupResults),
						experimentCase);
				break;

			case Random:
				xv = compute(graphIndex, graph,
						genRandomGrouppers(neededGroupResults), experimentCase);
				break;
			}

			timeMeasurement.end();
			logger.debug(
					"Computed multicast group parameters for a graph in {}",
					timeMeasurement.getDurationString());

			GroupResultDataAccess.insert(connection, new GroupExperiment(
					experimentCase, xv));
		}
	}

	private GroupExperimentValues compute(int graphIndex, Graph graph,
			NodeGroupper nodeGroupper, GroupExperimentCase xc) {

		List<Node> group = nodeGroupper.group(graph, xc.getGroupSize());

		double degree = TopologyAnalyser.averageDegree(graph, group);
		PathMetric diameter = TopologyAnalyser.diameter(graph, group);
		double clusteringCoefficient = TopologyAnalyser.clusteringCoefficient(
				graph, group);
		double density = TopologyAnalyser.nodeGroupoDensity(graph, group);

		return new GroupExperimentValues(graphIndex, degree, diameter.getHop(),
				diameter.getCost(), clusteringCoefficient, density);
	}

	private GroupExperimentValues compute(int graphIndex, Graph graph,
			ArrayList<NodeGroupper> grouppers, GroupExperimentCase xc) {

		SummaryStatistics degreeStat = new SummaryStatistics();
		SummaryStatistics diameterHopStat = new SummaryStatistics();
		SummaryStatistics diameterCostStat = new SummaryStatistics();
		SummaryStatistics clusteringCoefficientStat = new SummaryStatistics();
		SummaryStatistics densityStat = new SummaryStatistics();

		for (NodeGroupper groupper : grouppers) {
			GroupExperimentValues partialValues = compute(graphIndex, graph,
					groupper, xc);
			degreeStat.addValue(partialValues.getDegree());
			diameterHopStat.addValue(partialValues.getDiameterHop());
			diameterCostStat.addValue(partialValues.getDiameterCost());
			clusteringCoefficientStat.addValue(partialValues
					.getClusteringCoefficient());
			densityStat.addValue(partialValues.getClusteringCoefficient());
		}

		return new GroupExperimentValues(graphIndex, degreeStat.getMean(),
				diameterHopStat.getMean(), diameterCostStat.getMean(),
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
