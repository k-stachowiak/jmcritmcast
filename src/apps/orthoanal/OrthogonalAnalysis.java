package apps.orthoanal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import apps.algorthoanal.AlgorithmOrthogonalExperimentCase;
import apps.algorthoanal.AlgorithmOrthogonalExperimentResult;
import dto.EdgeDTO;
import dto.GroupDTO;
import dto.SubgraphDTO;
import helpers.TopologyAnalyser;
import helpers.metrprov.HopMetricProvider;
import model.topology.EdgeDefinition;
import model.topology.Graph;
import model.topology.Node;
import model.topology.Path;
import model.topology.SubGraph;
import model.topology.SubGraphToGraphAdapter;
import pfnd.MetricRelaxation;
import pfnd.Relaxation;
import pfnd.dkstr.DijkstraPathFinder;

public class OrthogonalAnalysis {

	private static final Logger logger = LogManager.getLogger(OrthogonalAnalysis.class);

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
			try (Connection connection = DriverManager.getConnection(CommonConfig.dbUri, CommonConfig.dbUser,
					CommonConfig.dbPass);) {

				OrthogonalAnalysisDataAccess.forEachResult(connection,
						new OrthogonalAnalysisDataAccess.ResultCallback() {

							@Override
							public void onResult(Graph graph, AlgorithmOrthogonalExperimentCase experimentCase,
									AlgorithmOrthogonalExperimentResult experimentResult) {
								computeMetrics(connection, graph, experimentCase, experimentResult);
							}
						});

			} catch (SQLException e) {
				e.printStackTrace();
				logger.fatal("Sql error: {}", e.getMessage());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void computeMetrics(Connection connection, Graph graph,
			AlgorithmOrthogonalExperimentCase experimentCase, AlgorithmOrthogonalExperimentResult experimentResult) {

		if (!experimentCase.isPerformed()) {
			return;
		}

		OrthogonalAnalysisDataAccess.putResultMetric(connection, "D", experimentResult.getId(),
				TopologyAnalyser.degreeStatistics(graph));

		SubGraph tree = generateTree(graph, experimentResult.getTreeDTO());
		List<StatisticalSummary> treeMetricStatistics = TopologyAnalyser.metricStatistics(tree);

		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m0T", experimentResult.getId(),
				treeMetricStatistics.get(0));
		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m1T", experimentResult.getId(),
				treeMetricStatistics.get(1));
		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m2T", experimentResult.getId(),
				treeMetricStatistics.get(2));

		List<Path> pathsConcrete = generatePaths(experimentResult.getGroupDTO(), graph, tree);
		List<SubGraph> paths = new ArrayList<>();
		for (Path path : pathsConcrete) {
			paths.add(path);
		}
		List<StatisticalSummary> pathMetricStatistics = TopologyAnalyser.metricStatistics(paths);

		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m0P", experimentResult.getId(),
				pathMetricStatistics.get(0));
		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m1P", experimentResult.getId(),
				pathMetricStatistics.get(1));
		OrthogonalAnalysisDataAccess.putResultMetric(connection, "m2P", experimentResult.getId(),
				pathMetricStatistics.get(2));
	}

	private static List<Path> generatePaths(GroupDTO groupDTO, Graph graph, SubGraph tree) {
		Relaxation relaxation = new MetricRelaxation(new HopMetricProvider());
		DijkstraPathFinder dpf = new DijkstraPathFinder(relaxation);
		SubGraphToGraphAdapter treeGraph = new SubGraphToGraphAdapter(tree);
		List<Path> result = new ArrayList<>();
		Node src = graph.getNode(groupDTO.getNodes().get(0).getId());
		for (int i = 1; i < groupDTO.getNodes().size(); ++i) {
			Node dst = graph.getNode(groupDTO.getNodes().get(i).getId());
			result.add(dpf.find(treeGraph, src, dst));
		}
		return result;
	}

	private static SubGraph generateTree(Graph graph, SubgraphDTO treeDTO) {
		Set<Integer> uniqueNodes = new HashSet<>();
		List<EdgeDefinition> edges = new ArrayList<>();
		for (EdgeDTO edge : treeDTO.getEdges()) {
			int from = edge.getNodeFrom();
			int to = edge.getNodeTo();
			uniqueNodes.add(from);
			uniqueNodes.add(to);
			edges.add(new EdgeDefinition(from, to));
		}
		List<Integer> nodes = new ArrayList<>(uniqueNodes);
		return new SubGraph(graph, nodes, edges);
	}

}
