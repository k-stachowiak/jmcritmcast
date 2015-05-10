package apps.topanal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.TopologyType;

public class TopologyAnalysis {

	private static final Logger logger = LogManager
			.getLogger(TopologyAnalysis.class);

	public static void forEachCase(Executor executor) {

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (Integer nodesCount : CommonConfig.nodesCounts) {
				for (TopologyType type : TopologyType.values()) {

					if (nodesCount < 3037 && type == TopologyType.Inet) {
						logger.trace("Too small graph for INET to support -- skipping.");
						continue;
					}

					executor.execute(new TopologyAnalysisRunnable(
							new TopologyExperimentCase(type, nodesCount),
							connection));

					connection.close();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
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
