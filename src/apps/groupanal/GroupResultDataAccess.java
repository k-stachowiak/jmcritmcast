package apps.groupanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;

public class GroupResultDataAccess {

	private static final Logger logger = LogManager
			.getLogger(GroupResultDataAccess.class);

	public static GroupExperimentValues selectResultForCase(
			Connection connection, GroupExperimentCase experimentCase) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT degree, diameter_hop, diameter_cost, clustering_coefficient, density "
						+ "FROM group_anal_results "
						+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ? AND graph_index = ?")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			if (!rs.next()) {
				return null;
			} else {
				return CommonDataAccess
						.groupResultValuesFromPartialResultSet(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void insert(Connection connection,
			GroupExperimentCase experimentCase,
			GroupExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO group_anal_results "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());
			prStatement.setDouble(6, experimentValues.getDegree());
			prStatement.setDouble(7, experimentValues.getDiameterHop());
			prStatement.setDouble(8, experimentValues.getDiameterCost());
			prStatement.setDouble(9,
					experimentValues.getClusteringCoefficient());
			prStatement.setDouble(10, experimentValues.getDensity());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void update(Connection connection,
			GroupExperimentCase experimentCase,
			GroupExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("UPDATE group_anal_results "
						+ "SET degree = ?, diameter_hop = ?, diameter_cost = ?, clustering_coefficient = ?, density = ? "
						+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ? AND graph_index = ?")) {

			prStatement.setDouble(1, experimentValues.getDegree());
			prStatement.setDouble(2, experimentValues.getDiameterHop());
			prStatement.setDouble(3, experimentValues.getDiameterCost());
			prStatement.setDouble(4,
					experimentValues.getClusteringCoefficient());
			prStatement.setDouble(5, experimentValues.getDensity());

			prStatement.setString(6, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(7, experimentCase.getNodesCount());
			prStatement.setInt(8, experimentCase.getGroupSize());
			prStatement.setString(9, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(10, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
