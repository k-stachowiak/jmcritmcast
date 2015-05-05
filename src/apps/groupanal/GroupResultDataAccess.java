package apps.groupanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;

public class GroupResultDataAccess {

	private static final Logger logger = LogManager
			.getLogger(GroupResultDataAccess.class);

	public static void deleteAll(Connection connection, GroupExperimentCase xc) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("DELETE FROM group_anal_results "
						+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ?")) {
			prStatement.setString(1, xc.getTopologyType().toString());
			prStatement.setInt(2, xc.getNodesCount());
			prStatement.setInt(3, xc.getGroupSize());
			prStatement.setString(4, xc.getNodeGroupperType().toString());

			logger.trace("About to execute statement: {}",
					prStatement.toString());

			prStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
	}

	public static List<GroupExperiment> selectResultsForCase(
			Connection connection, GroupExperimentCase tac) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT graph_index, degree, diameter_hop, diameter_cost, clustering_coefficient, density "
						+ "FROM group_anal_results "
						+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ?")) {

			ArrayList<GroupExperiment> result = new ArrayList<>();

			prStatement.setString(1, tac.getTopologyType().toString());
			prStatement.setInt(2, tac.getNodesCount());
			prStatement.setInt(3, tac.getGroupSize());
			prStatement.setString(4, tac.getNodeGroupperType().toString());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			while (rs.next()) {
				result.add(new GroupExperiment(tac, CommonDataAccess
						.groupResultValuesFromPartialResultSet(rs)));
			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void insert(Connection connection, GroupExperiment result) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO group_anal_results "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, result.getExperimentCase()
					.getTopologyType().toString());
			prStatement.setInt(2, result.getExperimentCase().getNodesCount());
			prStatement.setInt(3, result.getExperimentCase().getGroupSize());
			prStatement.setString(4, result.getExperimentCase()
					.getNodeGroupperType().toString());
			prStatement.setInt(5, result.getExperimentValues().getGraphIndex());
			prStatement.setDouble(6, result.getExperimentValues().getDegree());
			prStatement.setDouble(7, result.getExperimentValues()
					.getDiameterHop());
			prStatement.setDouble(8, result.getExperimentValues()
					.getDiameterCost());
			prStatement.setDouble(9, result.getExperimentValues()
					.getClusteringCoefficient());
			prStatement
					.setDouble(10, result.getExperimentValues().getDensity());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
