package apps.algorthoanal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dal.DTOMarshaller;
import dto.GroupDTO;
import dto.SubgraphDTO;

public class AlgorithmOrthogonalAnalysisDataAccess {

	private static final Logger logger = LogManager.getLogger(AlgorithmOrthogonalAnalysisDataAccess.class);

	public static void synchronize(Connection connection, AlgorithmOrthogonalExperimentCase experimentCase) {
		try (PreparedStatement selectStatement = connection.prepareStatement("SELECT id, performed FROM alg_cases "
				+ "WHERE type = ? AND nodes = ? AND " + "group_size = ? AND group_type = ? AND "
				+ "graph_index = ? AND tree_finder_type = ? AND " + "constraint1 = ? AND constraint2 = ?");
				PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO alg_cases("
						+ "type, nodes, group_size, group_type, graph_index, tree_finder_type, "
						+ "performed, constraint1, constraint2)" + "VALUES (?, ?, ?, ?, ?, ?, " + "?::bit, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			selectStatement.setString(1, experimentCase.getTopologyType().toString());
			selectStatement.setInt(2, experimentCase.getNodesCount());
			selectStatement.setInt(3, experimentCase.getGroupSize());
			selectStatement.setString(4, experimentCase.getNodeGroupperType().toString());
			selectStatement.setInt(5, experimentCase.getGraphIndex());
			selectStatement.setString(6, experimentCase.getTreeFinderType().toString());
			selectStatement.setDouble(7, experimentCase.getConstraint1());
			selectStatement.setDouble(8, experimentCase.getConstraint2());

			logger.trace("About to execute statement: {}", selectStatement.toString());
			ResultSet rs = selectStatement.executeQuery();

			if (!rs.next()) {
				logger.trace("No result found for case {}. Inserting new row.", experimentCase);
				insertStatement.setString(1, experimentCase.getTopologyType().toString());
				insertStatement.setInt(2, experimentCase.getNodesCount());
				insertStatement.setInt(3, experimentCase.getGroupSize());
				insertStatement.setString(4, experimentCase.getNodeGroupperType().toString());
				insertStatement.setInt(5, experimentCase.getGraphIndex());
				insertStatement.setString(6, experimentCase.getTreeFinderType().toString().toString());
				insertStatement.setString(7, "0");
				insertStatement.setDouble(8, experimentCase.getConstraint1());
				insertStatement.setDouble(9, experimentCase.getConstraint2());

				logger.trace("About to execute statement: {}", insertStatement.toString());
				insertStatement.executeUpdate();

				rs = insertStatement.getGeneratedKeys();
				if (!rs.next()) {
					throw new RuntimeException("Insert didn't generate a key");
				}
				experimentCase.setId(rs.getInt(1));
				experimentCase.setPerformed(false);

			} else {
				logger.trace("Experiment case row found");
				experimentCase.setId(rs.getInt(1));
				experimentCase.setPerformed(rs.getBoolean(2));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static void clearResults(Connection connection, AlgorithmOrthogonalExperimentCase experimentCase) {
		try (PreparedStatement prStatement = connection
				.prepareStatement("DELETE FROM alg_results WHERE alg_case_id = ?")) {

			prStatement.setInt(1, experimentCase.getId());

			logger.trace("About to execute statement: {}", prStatement.toString());
			prStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
	}

	public static void putResults(Connection connection, AlgorithmOrthogonalExperimentCase experimentCase,
			List<AlgorithmOrthogonalExperimentResult> experimentResults) {
		try (PreparedStatement updateStatement = connection.prepareStatement(
				"UPDATE alg_cases SET performed = ?::bit WHERE id = ?");
			 PreparedStatement insertStatement = connection.prepareStatement(
			    "INSERT INTO alg_results("
					 + "alg_case_id, tree, \"group\", \"time\") "
					 + "VALUES (?, ?, ?, ?)")) {
			
			for (AlgorithmOrthogonalExperimentResult result : experimentResults) {
				
				SubgraphDTO treeDTO = result.getTreeDTO();				
				DTOMarshaller<SubgraphDTO> subgraphMarshaller = new DTOMarshaller<>();
				String treeString = subgraphMarshaller.writeToString(treeDTO);
				
				GroupDTO groupDTO = result.getGroupDTO();
				DTOMarshaller<GroupDTO> groupMarshaller = new DTOMarshaller<>();
				String groupString = groupMarshaller.writeToString(groupDTO);
				
				insertStatement.setInt(1, result.getCaseId());
				insertStatement.setString(2, treeString);
				insertStatement.setString(3, groupString);
				insertStatement.setDouble(4, result.getSeconds());
				
				logger.trace("About to execute statement: {}",
						insertStatement.toString());
				insertStatement.executeUpdate();
			}
			
			updateStatement.setString(1, "1");
			updateStatement.setInt(2, experimentCase.getId());
			
			logger.trace("About to execute statement: {}",
					updateStatement.toString());
			updateStatement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

}
