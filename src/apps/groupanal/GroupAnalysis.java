package apps.groupanal;

import helpers.nodegrp.NodeGroupperType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.TopologyType;

public class GroupAnalysis {

	private static final Logger logger = LogManager
			.getLogger(GroupAnalysis.class);

	private static void forEachCase(Executor executor) {
		for (Integer nodesCount : CommonConfig.nodesCounts) {
			for (TopologyType tType : TopologyType.values()) {

				if (nodesCount < 3037 && tType == TopologyType.Inet) {
					logger.trace("Too small graph for INET to support -- skipping.");
					continue;
				}

				for (Integer groupSize : CommonConfig.groupSizes) {
					for (NodeGroupperType gType : NodeGroupperType.values()) {

						try (Connection connection = DriverManager
								.getConnection(CommonConfig.dbUri,
										CommonConfig.dbUser,
										CommonConfig.dbPass);) {

							executor.execute(new GroupAnalysisGatherExecutor(
									new GroupExperimentCase(tType, nodesCount,
											groupSize, gType), connection));

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
			forEachCase(Executors
					.newFixedThreadPool(CommonConfig.threadsPerWorker));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
