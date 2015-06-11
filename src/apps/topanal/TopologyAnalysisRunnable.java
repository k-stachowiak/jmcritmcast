package apps.topanal;

import helpers.TopologyAnalyser;

import java.sql.Connection;
import java.sql.SQLException;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.TimeMeasurement;
import dal.TopologyDAO;
import dto.GraphDTO;

public class TopologyAnalysisRunnable implements Runnable {

	private final TopologyExperimentCase experimentCase;
	private final Connection connection;

	private final GraphFactory graphFactory = new AdjacencyListFactory();
	private final TimeMeasurement timeMeasurement = new TimeMeasurement();

	private static final Logger logger = LogManager
			.getLogger(TopologyAnalysisRunnable.class);

	public TopologyAnalysisRunnable(TopologyExperimentCase experimentCase,
			Connection connection) {
		this.experimentCase = experimentCase;
		this.connection = connection;
	}

	@Override
	public void run() {

		logger.trace("Begin analysis for case {}", experimentCase);

		TopologyExperimentValues experimentValues = TopologyResultDataAccess
				.selectResultForCase(connection, experimentCase);

		if (experimentValues == null) {
			logger.trace("No result found, commencing...");
			experimentValues = new TopologyExperimentValues(null, null, null,
					null);
			TopologyResultDataAccess.insert(connection, experimentCase,
					experimentValues);
		} else if (experimentValues.isValid()) {
			logger.trace("Valid result for case found, aborting...");
			return;
		} else {
			logger.trace("Found invalid result, resuming...");
		}

		TopologyDAO topologyDAO = new TopologyDAO(connection);
		GraphDTO graphDTO;
		try {
			graphDTO = topologyDAO.select(experimentCase.getTopologyType(),
					experimentCase.getNodesCount(),
					experimentCase.getGraphIndex());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return;
		}
		Graph graph = graphFactory.createFromDTO(graphDTO);

		compute(graph, experimentValues);
	}

	private void compute(Graph graph, TopologyExperimentValues experimentValues) {

		if (experimentValues.getDegree() == null) {
			timeMeasurement.begin();
			experimentValues.setDegree(TopologyAnalyser.averageDegree(graph));
			timeMeasurement.end();
			logger.debug("Computed graph average degree in {}",
					timeMeasurement.getDurationString());
		}
		TopologyResultDataAccess.update(connection, experimentCase,
				experimentValues);

		if (experimentValues.getClusteringCoefficient() == null) {
			timeMeasurement.begin();
			experimentValues.setClusteringCoefficient(TopologyAnalyser
					.clusteringCoefficient(graph));
			timeMeasurement.end();
			logger.debug("Computed graph clustering coefficient in {}",
					timeMeasurement.getDurationString());
		}
		TopologyResultDataAccess.update(connection, experimentCase,
				experimentValues);
		/*
		if (experimentValues.getDiameterHop() == null
				|| experimentValues.getDiameterCost() == null) {
			timeMeasurement.begin();
			
			PathMetric diameterResult = TopologyAnalyser.diameter(graph);
			experimentValues.setDiameterHop(diameterResult.getHop());
			experimentValues.setDiameterCost(diameterResult.getCost());
			timeMeasurement.end();
			logger.debug("Computed graph diameter in {}",
					timeMeasurement.getDurationString());
			
		}
		TopologyResultDataAccess.update(connection, experimentCase,
				experimentValues);
		*/
		logger.debug("Skipping diameter metrics due to computation constraints.");
	}
}
