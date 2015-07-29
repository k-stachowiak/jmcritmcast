package apps.alganal;

import helpers.nodegrp.NodeGroupperType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tfind.TreeFinderType;
import apps.CommonConfig;
import dal.TopologyType;

public class AlgorithmAnalysis {

	private static final Logger logger = LogManager
			.getLogger(AlgorithmAnalysis.class);

	private static final int neededGraphResults = 5;

	private static void forEachCase(ExecutorService executor) {

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (Integer nodesCount : CommonConfig.nodesCounts) {

				for (TopologyType tType : TopologyType.values()) {

					if (nodesCount < 3037 && tType == TopologyType.Inet) {
						logger.trace("Too small graph for INET to support -- skipping.");
						continue;
					}

					for (Integer groupSize : CommonConfig.groupSizes) {

						for (NodeGroupperType gType : NodeGroupperType.values()) {

							for (double constraintsBase : CommonConfig.constraintBases) {

								for (TreeFinderType treeFinderType : TreeFinderType
										.values()) {

									for (int graphIndex = 1; graphIndex <= neededGraphResults; ++graphIndex) {

										executor.submit(new AlgorithmAnalysisRunnable(
												new AlgorithmExperimentCase(
														tType, nodesCount,
														groupSize, gType,
														graphIndex,
														constraintsBase,
														treeFinderType),
												connection));
									}

								}
							}
						}
					}
				}
			}

			executor.shutdown();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.fatal("Computation interrupted!");
		}

	}

	public static void main(String[] args) {

		int jobs = 1;
		if (args.length >= 2 && args[0].equals("-j")) {
			try (Scanner scanner = new Scanner(args[1]);) {
				if (scanner.hasNextInt()) {
					jobs = scanner.nextInt();
					logger.trace("Client requested {} jobs", jobs);
				} else {
					logger.debug(
							"Client specified incorrect jobs parameter: \"{}\"",
							args[1]);
				}
			}
		} else {
			logger.debug(
					"Client did not specify the jobs argument. {} assumed by default",
					jobs);
		}

		try {
			Class.forName("org.postgresql.Driver");
			forEachCase(Executors.newFixedThreadPool(jobs));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
