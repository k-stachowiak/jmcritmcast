package apps.alganal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;

public class AlgorithmResultDataAccess {

	private static final Logger logger = LogManager
			.getLogger(AlgorithmResultDataAccess.class);

	public static AlgorithmExperimentValues selectResultForCase(
			Connection connection, AlgorithmExperimentCase experimentCase) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT first_costs, success_count "
						+ "FROM alg_anal_results "
						+ "WHERE type = ? AND nodes = ? AND "
						+ "group_size = ? AND group_type = ? AND "
						+ "graph_index = ? AND constraint1 = ? AND constraint2 = ? AND "
						+ "tree_finder_type = ?")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());
			prStatement.setDouble(6, experimentCase.getConstraint1());
			prStatement.setDouble(7, experimentCase.getConstraint2());
			prStatement.setString(8, experimentCase.getTreeFinderType()
					.toString());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			if (!rs.next()) {
				return null;
			} else {
				return CommonDataAccess
						.algorithmResultValuesFromPartialResultSet(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void insert(Connection connection,
			AlgorithmExperimentCase experimentCase,
			AlgorithmExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO alg_anal_results "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());
			prStatement.setString(6, experimentCase.getTreeFinderType()
					.toString());
			prStatement.setString(7, CommonDataAccess
					.costListToString(experimentValues.getFirstCosts()));
			prStatement.setDouble(8, experimentValues.getSuccessCount());
			prStatement.setDouble(9, experimentCase.getConstraint1());
			prStatement.setDouble(10, experimentCase.getConstraint2());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}

	}

	public static void update(Connection connection,
			AlgorithmExperimentCase experimentCase,
			AlgorithmExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("UPDATE alg_anal_results "
						+ "SET first_costs = ?, success_count = ? "
						+ "WHERE type = ? AND nodes = ? AND "
						+ "group_size = ? AND group_type = ? AND "
						+ "graph_index = ? AND constraint1 = ? AND constraint2 = ? AND "
						+ "tree_finder_type = ?")) {

			prStatement.setString(1, CommonDataAccess
					.costListToString(experimentValues.getFirstCosts()));
			prStatement.setInt(2, experimentValues.getSuccessCount());
			prStatement.setString(3, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(4, experimentCase.getNodesCount());
			prStatement.setInt(5, experimentCase.getGroupSize());
			prStatement.setString(6, experimentCase.getNodeGroupperType()
					.toString());
			prStatement.setInt(7, experimentCase.getGraphIndex());
			prStatement.setDouble(8, experimentCase.getConstraint1());
			prStatement.setDouble(9, experimentCase.getConstraint2());
			prStatement.setString(10, experimentCase.getTreeFinderType()
					.toString());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
