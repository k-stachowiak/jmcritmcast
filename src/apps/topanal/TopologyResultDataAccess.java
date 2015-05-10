package apps.topanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;

public class TopologyResultDataAccess {

	private static final Logger logger = LogManager
			.getLogger(TopologyResultDataAccess.class);

	public static TopologyExperimentValues selectResultForCase(
			Connection connection, TopologyExperimentCase experimentCase) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT degree, diameter_hop, diameter_cost, clustering_coefficient "
						+ "FROM top_anal_results "
						+ "WHERE "
						+ "type = ? AND nodes = ? AND graph_index = ?")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			ResultSet rs = prStatement.executeQuery();

			if (!rs.next()) {
				return null;
			} else {
				return CommonDataAccess
						.topologyResultValuesFromPartialResultSet(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void insert(Connection connection,
			TopologyExperimentCase experimentCase,
			TopologyExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO top_anal_results "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGraphIndex());

			if (experimentValues.getDegree() == null) {
				prStatement.setNull(4, Types.DOUBLE);
			} else {
				prStatement.setDouble(4, experimentValues.getDegree());
			}

			if (experimentValues.getDiameterHop() == null) {
				prStatement.setNull(5, Types.DOUBLE);
			} else {
				prStatement.setDouble(5, experimentValues.getDiameterHop());
			}

			if (experimentValues.getDiameterCost() == null) {
				prStatement.setNull(6, Types.DOUBLE);
			} else {
				prStatement.setDouble(6, experimentValues.getDiameterCost());
			}

			if (experimentValues.getClusteringCoefficient() == null) {
				prStatement.setNull(7, Types.DOUBLE);
			} else {
				prStatement.setDouble(7,
						experimentValues.getClusteringCoefficient());
			}

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public static void update(Connection connection,
			TopologyExperimentCase experimentCase,
			TopologyExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("UPDATE top_anal_results "
						+ "SET degree = ?, diameter_hop = ?, diameter_cost = ?, clustering_coefficient = ? "
						+ "WHERE type = ? AND nodes = ? AND graph_index = ?")) {

			if (experimentValues.getDegree() == null) {
				prStatement.setNull(1, Types.DOUBLE);
			} else {
				prStatement.setDouble(1, experimentValues.getDegree());
			}

			if (experimentValues.getDiameterHop() == null) {
				prStatement.setNull(2, Types.DOUBLE);
			} else {
				prStatement.setDouble(2, experimentValues.getDiameterHop());
			}

			if (experimentValues.getDiameterCost() == null) {
				prStatement.setNull(3, Types.DOUBLE);
			} else {
				prStatement.setDouble(3, experimentValues.getDiameterCost());
			}

			if (experimentValues.getClusteringCoefficient() == null) {
				prStatement.setNull(4, Types.DOUBLE);
			} else {
				prStatement.setDouble(4,
						experimentValues.getClusteringCoefficient());
			}

			prStatement.setString(5, experimentCase.getTopologyType()
					.toString());
			prStatement.setInt(6, experimentCase.getNodesCount());
			prStatement.setInt(7, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}",
					prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
