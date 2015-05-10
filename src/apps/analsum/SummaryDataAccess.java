package apps.analsum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;
import apps.groupanal.GroupExperiment;
import apps.topanal.TopologyExperiment;

public class SummaryDataAccess {

	private static final Logger logger = LogManager
			.getLogger(SummaryDataAccess.class);

	public static List<TopologyExperiment> selectFinishedTopologyExperiments(
			Connection connection) {
		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT "
						+ "type, nodes, graph_index, "
						+ "degree, diameter_hop, diameter_cost, clustering_coefficient "
						+ "FROM top_anal_results "
						+ "WHERE "
						+ "degree IS NOT NULL AND diameter_hop IS NOT NULL AND diameter_cost IS NOT NULL AND clustering_coefficient IS NOT NULL ")) {

			ArrayList<TopologyExperiment> result = new ArrayList<>();

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			while (rs.next()) {
				result.add(new TopologyExperiment(CommonDataAccess
						.topologyResultCaseFromResultSet(rs), CommonDataAccess
						.topologyResultValuesFromFullResultSet(rs)));
			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static List<GroupExperiment> selectFinishedGroupExperiments(
			Connection connection) {
		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT "
						+ "type, nodes, group_size, group_type, graph_index, "
						+ "degree, diameter_hop, diameter_cost, clustering_coefficient, density "
						+ "FROM group_anal_results "
						+ "WHERE "
						+ "degree <> -1 AND diameter_hop <> -1 AND diameter_cost <> -1 AND clustering_coefficient <> -1 AND density <> -1")) {

			ArrayList<GroupExperiment> result = new ArrayList<>();

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			while (rs.next()) {
				result.add(new GroupExperiment(CommonDataAccess
						.groupResultCaseFromResultSet(rs), CommonDataAccess
						.groupResultValuesFromFullResultSet(rs)));
			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}
}
