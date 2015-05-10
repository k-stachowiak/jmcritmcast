package apps.topanal;

import helpers.PathMetric;
import helpers.TopologyAnalyser;

import java.sql.Connection;
import java.util.List;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.TimeMeasurement;
import dal.MultiBriteGraphStreamer;
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

		final int neededResults = 30;
		int currentGraphIndex = 1;

		logger.trace("Solving case : {}", experimentCase);

		MultiBriteGraphStreamer gs = new MultiBriteGraphStreamer("data/phd",
				experimentCase.getTopologyType(),
				experimentCase.getNodesCount(), 1000);

		// 1. Analyze already done.
		List<TopologyExperiment> doneResults = TopologyResultDataAccess
				.selectFinishedResultsForCase(connection, experimentCase);
		if (doneResults.size() >= neededResults) {
			logger.trace("Enough results already computed -- skipping entire case");
			return;
		}

		// ...skip graphs for the already performed experiments
		logger.trace("Skipping {} topologies for already computed results",
				doneResults.size());
		for (int i = 0; i < doneResults.size(); ++i) {
			gs.getNext();
			++currentGraphIndex;
		}

		// 2. Analyze partially done.
		List<TopologyExperiment> unfinishedResults = TopologyResultDataAccess
				.selectUnfinishedResultsForCase(connection, experimentCase);
		logger.trace("About to handle {} partially finished experiments",
				unfinishedResults.size());
		for (TopologyExperiment result : unfinishedResults) {
			compute(gs.getNext(), result, connection);
			++currentGraphIndex;
		}

		// 3. Analyze missing.
		logger.trace("About to handle {} new experiments", neededResults
				- (doneResults.size() + unfinishedResults.size()));
		for (int i = doneResults.size() + unfinishedResults.size() + 1; i < neededResults; ++i) {
			TopologyExperiment result = new TopologyExperiment(experimentCase,
					new TopologyExperimentValues(currentGraphIndex, null, null,
							null, null));
			TopologyResultDataAccess.insert(connection, result);
			compute(gs.getNext(), result, connection);
			++currentGraphIndex;
		}
	}

	private void compute(GraphDTO graphDTO, TopologyExperiment experiment,
			Connection connection) {

		Graph graph = graphFactory.createFromDTO(graphDTO);

		logger.entry(experiment);

		if (experiment.getExperimentValues().getDegree() == null) {
			timeMeasurement.begin();
			experiment.getExperimentValues().setDegree(
					TopologyAnalyser.averageDegree(graph));
			timeMeasurement.end();
			logger.debug("Computed graph average degree in {}",
					timeMeasurement.getDurationString());
		}
		TopologyResultDataAccess.update(connection, experiment);

		if (experiment.getExperimentValues().getClusteringCoefficient() == null) {
			timeMeasurement.begin();
			experiment.getExperimentValues().setClusteringCoefficient(
					TopologyAnalyser.clusteringCoefficient(graph));
			timeMeasurement.end();
			logger.debug("Computed graph clustering coefficient in {}",
					timeMeasurement.getDurationString());
		}
		TopologyResultDataAccess.update(connection, experiment);

		if (experiment.getExperimentValues().getDiameterHop() == null
				|| experiment.getExperimentValues().getDiameterCost() == null) {
			timeMeasurement.begin();
			PathMetric diameterResult = TopologyAnalyser.diameter(graph);
			experiment.getExperimentValues().setDiameterHop(
					diameterResult.getHop());
			experiment.getExperimentValues().setDiameterCost(
					diameterResult.getCost());
			timeMeasurement.end();
			logger.debug("Computed graph diameter in {}",
					timeMeasurement.getDurationString());
		}
		TopologyResultDataAccess.update(connection, experiment);
	}
}
