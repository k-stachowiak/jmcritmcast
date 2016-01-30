package apps.orthoanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.algorthoanal.AlgorithmOrthogonalExperimentCase;
import apps.algorthoanal.AlgorithmOrthogonalExperimentResult;
import dal.DTOMarshaller;
import dal.TopologyDAO;
import dal.TopologyType;
import dto.GraphDTO;
import dto.GroupDTO;
import dto.SubgraphDTO;
import helpers.nodegrp.NodeGroupperType;
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import tfind.TreeFinderType;

public class OrthogonalAnalysisDataAccess {

	private static final Logger logger = LogManager.getLogger(OrthogonalAnalysisDataAccess.class);

	public interface ResultCallback {
		void onResult(Graph graph, AlgorithmOrthogonalExperimentCase experimentCase,
				AlgorithmOrthogonalExperimentResult experimentResult);
	}

	public static void putResultMetric(
			Connection connection,
			String type,
			int resultId,
			double n,
			double mean,
			double stdev)
	{
		if (!existResultMetric(connection, resultId, type)) {
			try (PreparedStatement insertStatement = connection.prepareStatement(
				 "INSERT INTO alg_result_metrics(alg_result_id, mean, stdev, count, type) "
				 + "VALUES (?, ?, ?, ?, ?)");) {
				insertStatement.setInt(1, resultId);
				insertStatement.setDouble(2, mean);
				insertStatement.setDouble(3, stdev);
				insertStatement.setDouble(4, n);
				insertStatement.setString(5, type);
				logger.trace("About to execute statement: {}", insertStatement.toString());
				if (insertStatement.executeUpdate() != 1) {
					logger.warn("Insert query returned incorrect count");
				}			
			} catch (SQLException e) {
				e.printStackTrace();
				logger.fatal("Sql error: {}", e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}

	private static boolean existResultMetric(Connection connection, int resultId, String type) {
		try (PreparedStatement existsStatement = connection.prepareStatement(
				"SELECT id FROM alg_result_metrics WHERE alg_result_id = ? AND type = ?")) {
			existsStatement.setInt(1, resultId);
			existsStatement.setString(2, type);
			logger.trace("About to execute statement: {}", existsStatement.toString());
			ResultSet rs = existsStatement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static void putResultMetric(
			Connection connection,
			String type,
			int resultId,
			StatisticalSummary statistic) {
		putResultMetric(connection, type, resultId, statistic.getN(), statistic.getMean(),
				statistic.getStandardDeviation());
	}

	public static void forEachResult(Connection connection, ResultCallback callback) {

		TopologyDAO topologyDAO = new TopologyDAO(connection);
		GraphFactory graphFactory = new AdjacencyListFactory();
		DTOMarshaller<SubgraphDTO> subgraphMarshaller = new DTOMarshaller<>();
		DTOMarshaller<GroupDTO> groupMarshaller = new DTOMarshaller<>();

		try (PreparedStatement selectResultStatement = connection
				.prepareStatement("SELECT id, alg_case_id, tree, \"group\", \"time\" FROM alg_results");
				PreparedStatement selectCaseStatement = connection.prepareStatement(
						"SELECT id, type, nodes, group_size, group_type, graph_index, tree_finder_type, performed, constraint1, constraint2 "
								+ " FROM alg_cases WHERE id = ?");) {

			logger.trace("About to execute statement: {}", selectResultStatement.toString());
			ResultSet resultSet = selectResultStatement.executeQuery();
			while (resultSet.next()) {

				String treeString = resultSet.getString(3);
				SubgraphDTO treeDTO = subgraphMarshaller.readFromString(treeString, SubgraphDTO.class);
				String groupString = resultSet.getString(4);
				GroupDTO groupDTO = groupMarshaller.readFromString(groupString, GroupDTO.class);

				AlgorithmOrthogonalExperimentResult experimentResult = new AlgorithmOrthogonalExperimentResult(
						resultSet.getInt(2), treeDTO, groupDTO, resultSet.getDouble(5));
				experimentResult.setId(resultSet.getInt(1));

				selectCaseStatement.setInt(1, experimentResult.getCaseId());
				logger.trace("About to execute statement: {}", selectCaseStatement.toString());
				ResultSet caseSet = selectCaseStatement.executeQuery();
				if (!caseSet.next()) {
					continue;
				}

				AlgorithmOrthogonalExperimentCase experimentCase = new AlgorithmOrthogonalExperimentCase(
						TopologyType.valueOf(caseSet.getString(2)), caseSet.getInt(3), caseSet.getInt(4),
						NodeGroupperType.valueOf(caseSet.getString(5)), caseSet.getInt(6), caseSet.getDouble(9),
						caseSet.getDouble(10), TreeFinderType.valueOf(caseSet.getString(7)));
				experimentCase.setId(caseSet.getInt(1));
				experimentCase.setPerformed(caseSet.getBoolean(8));

				GraphDTO graphDTO = topologyDAO.select(experimentCase.getTopologyType(), experimentCase.getNodesCount(),
						experimentCase.getGraphIndex());
				Graph graph = graphFactory.createFromDTO(graphDTO);

				callback.onResult(graph, experimentCase, experimentResult);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
