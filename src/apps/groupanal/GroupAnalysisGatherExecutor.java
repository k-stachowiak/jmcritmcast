package apps.groupanal;

import helpers.PathMetric;
import helpers.TopologyAnalyser;
import helpers.nodegrp.CentroidNodeGroupper;
import helpers.nodegrp.DegreeNodeGroupper;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.sql.Connection;
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
import dal.MultiBriteGraphStreamer;
import dto.GraphDTO;

public class GroupAnalysisGatherExecutor implements GroupAnalysisExecutor {

	private final int neededGraphResults = 30;
	private final int neededGroupResults = 30;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager
			.getLogger(GroupAnalysisGatherExecutor.class);

	private static Random r = new Random(System.currentTimeMillis());

	/* (non-Javadoc)
	 * @see apps.groupanal.GroupAnalysisExecutor#execute(apps.groupanal.GroupExperimentCase, java.sql.Connection)
	 */
	@Override
	public void execute(GroupExperimentCase xc, Connection connection) {

		int currentGraphIndex = 1;

		logger.trace("Solving case : {}", xc);

		MultiBriteGraphStreamer gs = new MultiBriteGraphStreamer("data/phd",
				xc.getTopologyType(), xc.getNodesCount(), 1000);

		int computed = GroupResultDataAccess.selectResultsForCase(connection,
				xc).size();

		if (computed >= neededGraphResults) {
			return;
		}

		GroupResultDataAccess.deleteAll(connection, xc);
		while (currentGraphIndex <= neededGraphResults) {

			timeMeasurement.begin();

			GraphDTO graphDTO = gs.getNext();
			++currentGraphIndex;
			Graph graph = graphFactory.createFromDTO(graphDTO);

			GroupExperimentValues xv = null;

			switch (xc.getNodeGroupperType()) {
			case Degree:
				xv = compute(currentGraphIndex, graph,
						new DegreeNodeGroupper(), xc);
				break;

			case Centroid:
				xv = compute(currentGraphIndex, graph,
						genCentroidGrouppers(graph, neededGroupResults), xc);
				break;

			case Random:
				xv = compute(currentGraphIndex, graph,
						genRandomGrouppers(neededGroupResults), xc);
				break;
			}

			GroupResultDataAccess.insert(connection,
					new GroupExperiment(xc, xv));

			timeMeasurement.end();
			logger.debug(
					"Computed multicast group parameters for a graph in {}",
					timeMeasurement.getDurationString());
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

	private static ArrayList<NodeGroupper> genCentroidGrouppers(Graph graph,
			int count) {

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
