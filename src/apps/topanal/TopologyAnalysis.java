package apps.topanal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.TopologyType;

public class TopologyAnalysis {

	private static final int neededGraphResults = 30;

	private static final Logger logger = LogManager
			.getLogger(TopologyAnalysis.class);

	public static void forEachCase(ExecutorService executor) {

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (int graphIndex = 1; graphIndex <= neededGraphResults; ++graphIndex) {

				for (Integer nodesCount : CommonConfig.nodesCounts) {
					for (TopologyType type : TopologyType.values()) {

						if (nodesCount < 3037 && type == TopologyType.Inet) {
							logger.trace("Too small graph for INET to support -- skipping.");
							continue;
						}

						executor.execute(new TopologyAnalysisRunnable(
								new TopologyExperimentCase(type, nodesCount, graphIndex),
								connection));
					}
				}

			}
			
			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			Class.forName("org.postgresql.Driver");
			forEachCase(Executors
					.newFixedThreadPool(CommonConfig.threadsPerWorker));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.fatal("Computation interrupted!");
		}
	}
}
