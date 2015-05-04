package apps.groupanal;

import helpers.nodegrp.NodeGroupperType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.TopologyType;

public class GroupAnalysis {

	private static final Logger logger = LogManager
			.getLogger(GroupAnalysis.class);

	private static void forEachCase(GroupAnalysisExecutor executor) {
		for (TopologyType tType : TopologyType.values()) {
			for (Integer nodesCount : CommonConfig.nodesCounts) {

				if (nodesCount < 3037 && tType == TopologyType.Inet) {
					logger.trace("Too small graph for INET to support -- skipping.");
					continue;
				}

				for (Integer groupSize : CommonConfig.groupSizes) {
					for (NodeGroupperType gType : NodeGroupperType.values()) {

						try (Connection connection = DriverManager
								.getConnection(
										"jdbc:postgresql://localhost:5432/phd",
										"postgres", "admin");) {

							executor.execute(new GroupExperimentCase(tType,
									nodesCount, groupSize, gType), connection);

						} catch (SQLException e) {
							e.printStackTrace();
							logger.fatal("Sql error: {}", e.getMessage());
						}

					}
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
			forEachCase(new GroupAnalysisGatherExecutor());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
