package apps.toppush;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import dal.InputGraphStreamer;
import dal.MultiBriteGraphStreamer;
import dal.TopologyDAO;
import dal.TopologyType;
import dto.GraphDTO;

public class TopologyPush {

	private static final Logger logger = LogManager
			.getLogger(TopologyPush.class);

	public static void main(String[] args) {

		final int allCount = TopologyType.values().length
				* CommonConfig.nodesCounts.size() * 1000;
		int processedCount = 0;

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass)) {

			TopologyDAO topologyDAO = new TopologyDAO(connection);

			//for (TopologyType topologyType : TopologyType.values()) {
			TopologyType topologyType = TopologyType.Inet;
				for (int nodesCount : CommonConfig.nodesCounts) {
					
					if (nodesCount < 3037) {
						continue;
					}

					InputGraphStreamer inputGraphStreamer = new MultiBriteGraphStreamer(
							"data/phd", topologyType, nodesCount, 1000);

					for (int index = 1; index <= 1000; ++index) {
						GraphDTO graphDTO = inputGraphStreamer.getNext();
						topologyDAO.insert(graphDTO, topologyType, nodesCount,
								index);

						++processedCount;
						logger.trace(
								"Done {} {} {} ({}%)",
								topologyType.toString(),
								nodesCount,
								index,
								((double) processedCount / (double) allCount) * 100.0);
					}
				}
			//}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
	}

}
