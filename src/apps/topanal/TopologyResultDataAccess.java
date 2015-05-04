package apps.topanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TopologyResultDataAccess {

	private static final Logger logger = LogManager
			.getLogger(TopologyResultDataAccess.class);

	public static List<TopologyExperiment> selectFinishedResultsForCase(
			Connection connection, TopologyExperimentCase tac) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT graph_index, degree, diameter_hop, diameter_cost, clustering_coefficient "
						+ "FROM top_anal_results "
						+ "WHERE "
						+ "type = ? AND nodes = ? AND "
						+ "degree IS NOT NULL AND diameter_hop IS NOT NULL AND diameter_cost IS NOT NULL AND clustering_coefficient IS NOT NULL "
						+ "ORDER BY graph_index")) {

			ArrayList<TopologyExperiment> result = new ArrayList<>();

			prStatement.setString(1, tac.getTopologyType().toString());
			prStatement.setInt(2, tac.getNodesCount());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			while (rs.next()) {
				result.add(new TopologyExperiment(tac, resultValuesFromResultSet(rs)));
			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static List<TopologyExperiment> selectUnfinishedResultsForCase(
			Connection connection, TopologyExperimentCase tac) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT graph_index, degree, diameter_hop, diameter_cost, clustering_coefficient "
						+ "FROM top_anal_results "
						+ "WHERE "
						+ "type = ? AND nodes = ? AND "
						+ "(degree IS NULL OR diameter_hop IS NULL OR diameter_cost IS NULL OR clustering_coefficient IS NULL) "
						+ "ORDER BY graph_index")) {

			ArrayList<TopologyExperiment> result = new ArrayList<>();

			prStatement.setString(1, tac.getTopologyType().toString());
			prStatement.setInt(2, tac.getNodesCount());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			while (rs.next()) {
				result.add(new TopologyExperiment(tac, resultValuesFromResultSet(rs)));
			}

			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void update(Connection connection, TopologyExperiment result) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("UPDATE top_anal_results "
						+ "SET degree = ?, diameter_hop = ?, diameter_cost = ?, clustering_coefficient = ? "
						+ "WHERE type = ? AND nodes = ? AND graph_index = ?")) {

			if (result.getExperimentValues().getDegree() == null) {
				prStatement.setNull(1, Types.DOUBLE);
			} else {
				prStatement.setDouble(1, result.getExperimentValues()
						.getDegree());
			}

			if (result.getExperimentValues().getDiameterHop() == null) {
				prStatement.setNull(2, Types.DOUBLE);
			} else {
				prStatement.setDouble(2, result.getExperimentValues()
						.getDiameterHop());
			}

			if (result.getExperimentValues().getDiameterCost() == null) {
				prStatement.setNull(3, Types.DOUBLE);
			} else {
				prStatement.setDouble(3, result.getExperimentValues()
						.getDiameterCost());
			}

			if (result.getExperimentValues().getClusteringCoefficient() == null) {
				prStatement.setNull(4, Types.DOUBLE);
			} else {
				prStatement.setDouble(4, result.getExperimentValues()
						.getClusteringCoefficient());
			}

			prStatement.setString(5, result.getExperimentCase()
					.getTopologyType().toString());
			prStatement.setInt(6, result.getExperimentCase().getNodesCount());
			prStatement.setInt(7, result.getExperimentValues().getGraphIndex());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void insert(Connection connection, TopologyExperiment result) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO top_anal_results "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, result.getExperimentCase()
					.getTopologyType().toString());
			prStatement.setInt(2, result.getExperimentCase().getNodesCount());
			prStatement.setInt(3, result.getExperimentValues().getGraphIndex());

			if (result.getExperimentValues().getDegree() == null) {
				prStatement.setNull(4, Types.DOUBLE);
			} else {
				prStatement.setDouble(4, result.getExperimentValues()
						.getDegree());
			}

			if (result.getExperimentValues().getDiameterHop() == null) {
				prStatement.setNull(5, Types.DOUBLE);
			} else {
				prStatement.setDouble(5, result.getExperimentValues()
						.getDiameterHop());
			}

			if (result.getExperimentValues().getDiameterCost() == null) {
				prStatement.setNull(6, Types.DOUBLE);
			} else {
				prStatement.setDouble(6, result.getExperimentValues()
						.getDiameterCost());
			}

			if (result.getExperimentValues().getClusteringCoefficient() == null) {
				prStatement.setNull(7, Types.DOUBLE);
			} else {
				prStatement.setDouble(7, result.getExperimentValues()
						.getClusteringCoefficient());
			}

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private static TopologyExperimentValues resultValuesFromResultSet(ResultSet rs)
			throws SQLException {
		return new TopologyExperimentValues(rs.getInt(1), (Double) rs.getObject(2),
				(Double) rs.getObject(3), (Double) rs.getObject(4),
				(Double) rs.getObject(5));
	}

}
