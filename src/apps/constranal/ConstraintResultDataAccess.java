package apps.constranal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;
import apps.analsum.cstr.SummaryConstraintResults;
import apps.groupanal.GroupResultDataAccess;
import dal.TopologyType;

public class ConstraintResultDataAccess {

	private static final Logger logger = LogManager.getLogger(GroupResultDataAccess.class);

	public static ConstraintExperimentValues selectResultForCase(Connection connection,
			ConstraintExperimentCase experimentCase) {
		
		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT range0_min, range0_max, range1_min, range1_max "
						+ "FROM cstr_anal_results "
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
						.constraintResultValuesFromPartialResultSet(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			return null;
		}
	}

	public static void insert(Connection connection, ConstraintExperimentCase experimentCase,
			ConstraintExperimentValues experimentValues) {

		try (PreparedStatement prStatement = connection
				.prepareStatement("INSERT INTO cstr_anal_results " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)")) {

			prStatement.setString(1, experimentCase.getTopologyType().toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType().toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());
			prStatement.setDouble(6, experimentValues.getRange0().getMin());
			prStatement.setDouble(7, experimentValues.getRange0().getMax());
			prStatement.setDouble(8, experimentValues.getRange1().getMin());
			prStatement.setDouble(9, experimentValues.getRange1().getMax());

			logger.trace("About to execute statement: {}", prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}

	}

	public static void update(Connection connection, ConstraintExperimentCase experimentCase,
			ConstraintExperimentValues experimentValues) {
		try (PreparedStatement prStatement = connection.prepareStatement("UPDATE cstr_anal_results "
				+ "SET range0_min = ?, range0_max = ?, range1_min = ?, range1_max = ? "
				+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ? AND graph_index = ?")) {

			prStatement.setDouble(1, experimentValues.getRange0().getMin());
			prStatement.setDouble(2, experimentValues.getRange0().getMax());
			prStatement.setDouble(3, experimentValues.getRange1().getMin());
			prStatement.setDouble(4, experimentValues.getRange1().getMax());

			prStatement.setString(5, experimentCase.getTopologyType().toString());
			prStatement.setInt(6, experimentCase.getNodesCount());
			prStatement.setInt(7, experimentCase.getGroupSize());
			prStatement.setString(8, experimentCase.getNodeGroupperType().toString());
			prStatement.setInt(9, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}", prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public static Map<TopologyType, SummaryConstraintResults> selectResults(Connection connection, int nodesCount, int groupSize,
			String groupperName) {
		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT type, range0_min, range0_max, range1_min, range1_max "
						+ "FROM cstr_anal_results "
						+ "WHERE nodes = ? AND group_size = ? AND group_type = ? "
						+ "AND range0_min <> -1 AND range0_max <> -1 AND range1_min <> -1 AND range1_max <> -1")) {
			
			prStatement.setInt(1, nodesCount);
			prStatement.setInt(2, groupSize);
			prStatement.setString(3, groupperName);

			Map<TopologyType, SummaryConstraintResults> result = new LinkedHashMap<>();
			for (TopologyType topologyType : TopologyType.values()) {
				result.put(topologyType, new SummaryConstraintResults());
			}
			
			ResultSet rs = prStatement.executeQuery();
			while (rs.next()) {
				SummaryConstraintResults summaryConstraintsResults = result.get(TopologyType.valueOf(rs.getString(1)));
				summaryConstraintsResults.put(rs.getDouble(2), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5));
			}			
			return result;
			
		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}
