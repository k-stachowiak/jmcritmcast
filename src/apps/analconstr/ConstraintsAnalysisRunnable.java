package apps.analconstr;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.TimeMeasurement;
import apps.groupanal.GroupAnalysisRunnable;
import dal.TopologyDAO;
import dto.GraphDTO;
import helpers.TopologyAnalyser;
import helpers.cstrch.FengGroupConstraintsChooser;
import helpers.nodegrp.CentroidNodeGroupper;
import helpers.nodegrp.DegreeNodeGroupper;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;

public class ConstraintsAnalysisRunnable implements Runnable {

	private final ConstraintExperimentCase experimentCase;
	private final Connection connection;

	private final int neededConstraintResults = 10;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager.getLogger(GroupAnalysisRunnable.class);

	private static Random r = new Random(System.currentTimeMillis());

	public ConstraintsAnalysisRunnable(ConstraintExperimentCase constraintExperimentCase, Connection connection) {
		this.experimentCase = constraintExperimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {

		logger.trace("Begin analysis for case {}", experimentCase);

		ConstraintExperimentValues experimentValues = ConstraintResultDataAccess.selectResultForCase(connection,
				experimentCase);

		if (experimentValues == null) {
			logger.trace("No result found, commencing...");
			ConstraintResultDataAccess.insert(connection, experimentCase,
					new ConstraintExperimentValues(new ConstraintExperimentValues.Range(-1, -1),
							new ConstraintExperimentValues.Range(-1, -1),
							new ConstraintExperimentValues.Range(-1, -1)));
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
			experimentValues = compute(graph, genCentroidGrouppers(graph, neededConstraintResults));
			break;

		case Random:
			experimentValues = compute(graph, genRandomGrouppers(neededConstraintResults));
			break;
		}

		timeMeasurement.end();
		logger.debug("Computed constraint estimates in {}", timeMeasurement.getDurationString());

		ConstraintResultDataAccess.update(connection, experimentCase, experimentValues);
	}

	private ConstraintExperimentValues compute(Graph graph, NodeGroupper nodeGroupper) {
		List<Node> group = nodeGroupper.group(graph, experimentCase.getGroupSize());
		FengGroupConstraintsChooser cch = new FengGroupConstraintsChooser(-1, pathFinderFactory);
		List<FengGroupConstraintsChooser.Range> ranges = cch.chooseRanges(graph, group);
		return new ConstraintExperimentValues(
				new ConstraintExperimentValues.Range(ranges.get(0).getMin(), ranges.get(0).getMax()),
				new ConstraintExperimentValues.Range(ranges.get(1).getMin(), ranges.get(1).getMax()),
				new ConstraintExperimentValues.Range(ranges.get(2).getMin(), ranges.get(2).getMax()));
	}

	private ConstraintExperimentValues compute(Graph graph, ArrayList<NodeGroupper> grouppers) {

		SummaryStatistics min0Stat = new SummaryStatistics();
		SummaryStatistics max0Stat = new SummaryStatistics();
		SummaryStatistics min1Stat = new SummaryStatistics();
		SummaryStatistics max1Stat = new SummaryStatistics();
		SummaryStatistics min2Stat = new SummaryStatistics();
		SummaryStatistics max2Stat = new SummaryStatistics();

		for (NodeGroupper groupper : grouppers) {
			ConstraintExperimentValues partialValues = compute(graph, groupper);
			min0Stat.addValue(partialValues.getRange0().getMin());
			max0Stat.addValue(partialValues.getRange0().getMax());
			min1Stat.addValue(partialValues.getRange1().getMin());
			max1Stat.addValue(partialValues.getRange1().getMax());
			min2Stat.addValue(partialValues.getRange2().getMin());
			max2Stat.addValue(partialValues.getRange2().getMax());
		}

		return new ConstraintExperimentValues(
				new ConstraintExperimentValues.Range(min0Stat.getMean(), max0Stat.getMean()),
				new ConstraintExperimentValues.Range(min1Stat.getMean(), max1Stat.getMean()),
				new ConstraintExperimentValues.Range(min2Stat.getMean(), max2Stat.getMean()));
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
