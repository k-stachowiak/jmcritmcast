package apps.analconstr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonDataAccess;
import apps.groupanal.GroupResultDataAccess;

public class ConstraintResultDataAccess {

	private static final Logger logger = LogManager.getLogger(GroupResultDataAccess.class);

	public static ConstraintExperimentValues selectResultForCase(Connection connection,
			ConstraintExperimentCase experimentCase) {
		
		try (PreparedStatement prStatement = connection
				.prepareStatement("SELECT range0_min, range0_max, range1_min, range1_max, range2_min, range2_max "
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
				.prepareStatement("INSERT INTO cstr_anal_results " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

			prStatement.setString(1, experimentCase.getTopologyType().toString());
			prStatement.setInt(2, experimentCase.getNodesCount());
			prStatement.setInt(3, experimentCase.getGroupSize());
			prStatement.setString(4, experimentCase.getNodeGroupperType().toString());
			prStatement.setInt(5, experimentCase.getGraphIndex());
			prStatement.setDouble(6, experimentValues.getRange0().getMin());
			prStatement.setDouble(7, experimentValues.getRange0().getMax());
			prStatement.setDouble(8, experimentValues.getRange1().getMin());
			prStatement.setDouble(9, experimentValues.getRange1().getMax());
			prStatement.setDouble(10, experimentValues.getRange2().getMin());
			prStatement.setDouble(11, experimentValues.getRange2().getMax());

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
				+ "SET range0_min = ?, range0_max = ?, range1_min = ?, range1_max = ?, range2_min = ?, range2_max = ? "
				+ "WHERE type = ? AND nodes = ? AND group_size = ? AND group_type = ? AND graph_index = ?")) {

			prStatement.setDouble(1, experimentValues.getRange0().getMin());
			prStatement.setDouble(2, experimentValues.getRange0().getMax());
			prStatement.setDouble(3, experimentValues.getRange1().getMin());
			prStatement.setDouble(4, experimentValues.getRange1().getMax());
			prStatement.setDouble(5, experimentValues.getRange2().getMin());
			prStatement.setDouble(6, experimentValues.getRange2().getMax());

			prStatement.setString(7, experimentCase.getTopologyType().toString());
			prStatement.setInt(8, experimentCase.getNodesCount());
			prStatement.setInt(9, experimentCase.getGroupSize());
			prStatement.setString(10, experimentCase.getNodeGroupperType().toString());
			prStatement.setInt(11, experimentCase.getGraphIndex());

			logger.trace("About to execute statement: {}", prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			logger.fatal("Sql error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
