package apps.topanal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.TopologyType;

public class TopologyAnalysis {

	private static final Logger logger = LogManager
			.getLogger(TopologyAnalysis.class);

	private static void forEachCase(TopologyAnalysisExecutor executor) {

		for (TopologyType type : TopologyType.values()) {
			for (Integer nodesCount : CommonConfig.nodesCounts) {
				try (Connection connection = DriverManager.getConnection(
						"jdbc:postgresql://localhost:5432/phd", "postgres",
						"admin")) {

					if (nodesCount < 3037 && type == TopologyType.Inet) {
						logger.trace("Too small graph for INET to support -- skipping.");
						continue;
					}

					executor.execute(new TopologyExperimentCase(type,
							nodesCount), connection);

					connection.close();

				} catch (SQLException e) {
					e.printStackTrace();
					logger.fatal("Sql error: {}", e.getMessage());
				}
			}
		}
	}

	public static void main(String[] args) {

		try {
			Class.forName("org.postgresql.Driver");
			forEachCase(new TopologyAnalysisGatherExecutor());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
