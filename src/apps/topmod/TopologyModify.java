package apps.topmod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import apps.toppush.TopologyPush;
import dal.TopologyDAO;
import dal.TopologyDAO.EdgeStatistic;
import dal.TopologyDAO.MetricGenerator;
import dal.TopologyType;

public class TopologyModify {

	private static final Logger logger = LogManager.getLogger(TopologyPush.class);

	private static Random random = new Random();

	static class NormalMetricGenerator implements MetricGenerator {

		private final Random random;
		private final double mean;
		private final double stdev;

		public NormalMetricGenerator(Random random, double mean, double stdev) {
			this.random = random;
			this.mean = mean;
			this.stdev = stdev;
		}

		@Override
		public double getNext() {
			double value = random.nextGaussian() * stdev + mean;
			return (value < 0) ? 0.0 : value;
		}

	}

	static class UniformMetricGenerator implements MetricGenerator {
		private final Random random;

		public UniformMetricGenerator(Random random) {
			this.random = random;
		}

		@Override
		public double getNext() {
			return random.nextDouble();
		}
	}

	public static void main(String[] args) {
		
		////HashMap<Integer, HashMap<TopologyType, Boolean>> needUpdateMetric1 = new HashMap<>();
		////HashMap<Integer, HashMap<TopologyType, Boolean>> needUpdateMetric2 = new HashMap<>();

		try (Connection connection = DriverManager.getConnection(CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass)) {

			final TopologyDAO topologyDao = new TopologyDAO(connection);

			for (int nodesCount : CommonConfig.nodesCounts) {
				
				////needUpdateMetric1.put(nodesCount, new HashMap<>());
				////needUpdateMetric2.put(nodesCount, new HashMap<>());

				logger.trace("Nodes count = {} BEGIN", nodesCount);

				UniformMetricGenerator uniformMetricGenerator = new UniformMetricGenerator(random);

				if (nodesCount >= 3037) {

					logger.trace("Nodes count big enough for INET");
					EdgeStatistic stats = topologyDao.selectStatistics(TopologyType.ASWaxman, nodesCount, 100.0);
					logger.trace("Statistics generated");
					
					NormalMetricGenerator normalMetricGenerator = new NormalMetricGenerator(random,
							stats.getMetric1().getMean(), stats.getMetric1().getStandardDeviation());
					
					if (topologyDao.needToUpdateMetric(TopologyType.Inet, nodesCount, 1)) {
						topologyDao.updateMetric(TopologyType.Inet, nodesCount, 1, normalMetricGenerator);
						logger.trace("Updated INET normal metric (1) for node count " + nodesCount);
						////logger.trace("Need to update metric (1) for INET for " + nodesCount + "nodes.");
						////needUpdateMetric1.get(nodesCount).put(TopologyType.Inet, true);
					} else {
						logger.trace("INET normal metric (1) already updated for node count " + nodesCount);
						////logger.trace("NOT Need to update metric (1) for INET for " + nodesCount + "nodes.");
						////needUpdateMetric1.get(nodesCount).put(TopologyType.Inet, false);
					}
					if (topologyDao.needToUpdateMetric(TopologyType.Inet, nodesCount, 2)) {
						topologyDao.updateMetric(TopologyType.Inet, nodesCount, 2, uniformMetricGenerator);
						logger.trace("Updated INET uniform metric (2) for node count " + nodesCount);
						////logger.trace("Need to update metric (2) for INET for " + nodesCount + "nodes.");
						////needUpdateMetric2.get(nodesCount).put(TopologyType.Inet, true);
					} else {
						logger.trace("INET uniform metric (2) already updated for node count " + nodesCount);
						////logger.trace("NOT Need to update metric (2) for INET for " + nodesCount + "nodes.");
						////needUpdateMetric2.get(nodesCount).put(TopologyType.Inet, false);
					}
				} else {
					logger.trace("Nodes count NOT big enough for INET");
				}

				if (topologyDao.needToUpdateMetric(TopologyType.ASWaxman, nodesCount, 2)) {
					topologyDao.updateMetric(TopologyType.ASWaxman, nodesCount, 2, uniformMetricGenerator);
					logger.trace("Updated Waxman uniform metric (2) for node count " + nodesCount);
					////logger.trace("Need to update metric (2) for Waxman for " + nodesCount + "nodes.");
					////needUpdateMetric2.get(nodesCount).put(TopologyType.ASWaxman, true);
				} else {
					logger.trace("Waxman uniform metric (2) already updated for node count " + nodesCount);
					////logger.trace("NOT Need to update metric (2) for Waxman for " + nodesCount + "nodes.");
					////needUpdateMetric2.get(nodesCount).put(TopologyType.ASWaxman, false);
				}

				if (topologyDao.needToUpdateMetric(TopologyType.ASBarabasi, nodesCount, 2)) {
					topologyDao.updateMetric(TopologyType.ASBarabasi, nodesCount, 2, uniformMetricGenerator);
					logger.trace("Updated Barabasi uniform metric (2) for node count " + nodesCount);
					////logger.trace("Need to update metric (2) for Waxman for " + nodesCount + "nodes.");
					////needUpdateMetric2.get(nodesCount).put(TopologyType.ASBarabasi, true);
				} else {
					logger.trace("Barabasi uniform metric (2) already updated for node count " + nodesCount);
					////logger.trace("NOT Need to update metric (2) for Waxman for " + nodesCount + "nodes.");
					////needUpdateMetric2.get(nodesCount).put(TopologyType.ASBarabasi, false);
				}

				logger.trace("Nodes count = {} END", nodesCount);
			}	

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
		
		////try (PrintWriter canLogWriter = new PrintWriter("canLog", "UTF-8")) {
			
		////	for (Map.Entry<Integer, HashMap<TopologyType, Boolean>> entry1: needUpdateMetric1.entrySet()) {
		////		for (Map.Entry<TopologyType, Boolean> entry2 : entry1.getValue().entrySet()) {
		////			canLogWriter.println("Metric 1, " + entry1.getKey() + " nodes, " + entry2.getKey().toString() + " : " + entry2.getValue());
		////		}
		////	}
			
		////	for (Map.Entry<Integer, HashMap<TopologyType, Boolean>> entry1: needUpdateMetric2.entrySet()) {
		////		for (Map.Entry<TopologyType, Boolean> entry2 : entry1.getValue().entrySet()) {
		////			canLogWriter.println("Metric 2, " + entry1.getKey() + " nodes, " + entry2.getKey().toString() + " : " + entry2.getValue());
		////		}
		////	}
			
		////} catch (FileNotFoundException | UnsupportedEncodingException e) {
		////	e.printStackTrace();
		////	logger.fatal("File writer error : {}", e.getMessage());
		////}
	}
}