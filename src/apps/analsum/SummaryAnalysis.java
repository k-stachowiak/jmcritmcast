package apps.analsum;

import helpers.nodegrp.NodeGroupperType;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tfind.TreeFinderType;
import apps.CommonConfig;
import apps.alganal.AlgorithmExperiment;
import apps.groupanal.GroupExperiment;
import apps.topanal.TopologyExperiment;
import dal.TopologyType;

public class SummaryAnalysis {

	private static final Logger logger = LogManager
			.getLogger(SummaryAnalysis.class);

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
			// printTopologySummary(System.out);
			// printGroupSummary(System.out);
			printAlgorithmSummary(System.out);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void printGroupSummaryForAttribute(PrintStream out,
			SummaryGroupResultTable resultsTable,
			SummaryGroupResultAttributeSelector attributeSelector) {
		/*
		 * Expected data:
		 * for(topology type 1, nodes count 1)
		 * M	groupper1	groupper2	...
		 * 4	attr		attr		...
		 * 8	attr		attr		...
		 * ...	...			...			...
		 * 
		 * ...
		 * 
		 * for(topology type x, nodes count y)
		 * ...
		 */

		for (TopologyType topologyType : TopologyType.values()) {
			for (int nodesCount : CommonConfig.nodesCounts) {

				// 1. Print table header
				out.printf("Attribute: %s, Topology: %s, Nodes count: %d",
						attributeSelector.getName(), topologyType.toString(),
						nodesCount);
				out.println();

				// 2. Print data header
				out.print("M\t");
				for (NodeGroupperType nodeGroupperType : NodeGroupperType
						.values()) {
					out.printf("%s(n)\t", nodeGroupperType.toString());
					out.printf("%s(mean)\t", nodeGroupperType.toString());
					out.printf("%s(ci)\t", nodeGroupperType.toString());
				}
				out.println();

				// 3. Print data rows
				for (int groupSize : CommonConfig.groupSizes) {
					out.printf("%d\t", groupSize);
					Iterator<Entry<NodeGroupperType, SummaryGroupResults>> rowIterator = resultsTable
							.selectRow(topologyType, nodesCount, groupSize);
					while (rowIterator.hasNext()) {
						Entry<NodeGroupperType, SummaryGroupResults> entry = rowIterator
								.next();
						SummaryStatistics attribute = attributeSelector
								.select(entry.getValue());

						if (attribute.getN() == 0) {
							out.print("0\t-\t-\t");
						} else if (attribute.getN() == 1) {
							out.printf("1\t%f\t-\t", attribute.getMean());
						} else {
							out.printf(
									"%d\t%f\t%f\t",
									attribute.getN(),
									attribute.getMean(),
									getConfidenceIntervalWidth(attribute,
											CommonConfig.significance));
						}
					}
					out.println();
				}
			}
		}
	}

	private static void printGroupSummary(PrintStream out) {

		SummaryGroupResultTable resultsTable = new SummaryGroupResultTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (GroupExperiment experiment : SummaryDataAccess
					.selectFinishedGroupExperiments(connection)) {
				SummaryGroupResults results = resultsTable
						.selectResults(experiment.getExperimentCase());
				results.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printGroupSummaryForAttribute(out, resultsTable,
				new SummaryGroupResultAttributeSelectorDegree());
		printGroupSummaryForAttribute(out, resultsTable,
				new SummaryGroupResultAttributeSelectorDiameterHop());
		printGroupSummaryForAttribute(out, resultsTable,
				new SummaryGroupResultAttributeSelectorDiameterCost());
		printGroupSummaryForAttribute(out, resultsTable,
				new SummaryGroupResultAttributeSelectorClusteringCoefficient());
		printGroupSummaryForAttribute(out, resultsTable,
				new SummaryGroupResultAttributeSelectorDensity());
	}

	private static void printTopologySummaryForAttribute(PrintStream out,
			SummaryTopologyResultTable resultsTable,
			SummaryTopologyResultAttributeSelector attributeSelector) {

		/* 
		 * Expected data:
		 * N	top1	top2	...
		 * 50	attr	attr	...
		 * 150  attr	attr	...
		 * ...	...		...		...
		 */

		// 1. Print table header
		out.printf("Attribute: %s", attributeSelector.getName());
		out.println();

		// 2. Print data header
		out.print("N\t");
		for (TopologyType topologyType : TopologyType.values()) {
			out.printf("%s(n)\t", topologyType.toString());
			out.printf("%s(mean)\t", topologyType.toString());
			out.printf("%s(ci)\t", topologyType.toString());
		}
		out.println();

		// 3. Print data rows
		for (int nodesCount : CommonConfig.nodesCounts) {
			out.printf("%d\t", nodesCount);
			Iterator<Entry<TopologyType, SummaryTopologyResults>> rowIterator = resultsTable
					.selectRow(nodesCount);
			while (rowIterator.hasNext()) {
				Entry<TopologyType, SummaryTopologyResults> entry = rowIterator
						.next();
				SummaryStatistics attribute = attributeSelector.select(entry
						.getValue());

				if (attribute.getN() == 0) {
					out.print("0\t-\t-\t");
				} else if (attribute.getN() == 1) {
					out.printf("1\t%f\t-\t", attribute.getMean());
				} else {
					out.printf(
							"%d\t%f\t%f\t",
							attribute.getN(),
							attribute.getMean(),
							getConfidenceIntervalWidth(attribute,
									CommonConfig.significance));
				}
			}
			out.println();
		}

	}

	private static void printTopologySummary(PrintStream out) {

		SummaryTopologyResultTable resultsTable = new SummaryTopologyResultTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (TopologyExperiment experiment : SummaryDataAccess
					.selectFinishedTopologyExperiments(connection)) {
				SummaryTopologyResults results = resultsTable
						.selectResults(experiment.getExperimentCase());
				results.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelectorDegree());
		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelectorDiameterHop());
		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelectorDiameterCost());
		printTopologySummaryForAttribute(
				out,
				resultsTable,
				new SummaryTopologyResultAttributeSelectorClusteringCoefficient());
	}

	private static void printAlgorithmSummaryForAttribute(PrintStream out,
			SummaryAlgorithmResultTable resultsTable,
			SummaryAlgorithmResultAttributeSelector attributeSelector) {
		/*
		 * Expected data:
		 * for(topology type 1, nodes count 1, groupper type 1, constraint base 1)
		 * M	alg1	alg2	...
		 * 4	attr	attr	...
		 * 8	attr	attr	...
		 * ...	...		...		...
		 * 
		 * ...
		 * 
		 * for(topology type x, nodes count y)
		 * ...
		 */
		for (TopologyType topologyType : TopologyType.values()) {
			for (int nodesCount : CommonConfig.nodesCounts) {
				for (NodeGroupperType nodeGroupperType : NodeGroupperType
						.values()) {
					for (double constraintBase : CommonConfig.constraintBases) {

						// 1. Print table header
						out.printf(
								"Attribute: %s, Topology: %s, Nodes count: %d, Groupper: %s",
								attributeSelector.getName(),
								topologyType.toString(), nodesCount,
								nodeGroupperType.toString());
						out.println();

						// 2. Print data header
						out.print("M\t");
						for (TreeFinderType treeFinderType : TreeFinderType
								.values()) {
							out.printf("%s(n)\t", treeFinderType.toString());
							out.printf("%s(mean)\t", treeFinderType.toString());
							out.printf("%s(ci)\t", treeFinderType.toString());
						}
						out.println();

						// 3. Print data rows
						for (int groupSize : CommonConfig.groupSizes) {
							out.printf("%d\t", groupSize);
							Iterator<Entry<TreeFinderType, SummaryAlgorithmResults>> rowIterator = resultsTable
									.selectRow(topologyType, nodesCount,
											groupSize, nodeGroupperType,
											constraintBase);
							while (rowIterator.hasNext()) {
								Entry<TreeFinderType, SummaryAlgorithmResults> entry = rowIterator
										.next();
								SummaryStatistics attribute = attributeSelector
										.select(entry.getValue());

								if (attribute.getN() == 0) {
									out.print("0\t-\t-\t");
								} else if (attribute.getN() == 1) {
									out.printf("1\t%f\t-\t",
											attribute.getMean());
								} else {
									out.printf(
											"%d\t%f\t%f\t",
											attribute.getN(),
											attribute.getMean(),
											getConfidenceIntervalWidth(
													attribute,
													CommonConfig.significance));
								}
							}
							out.println();
						}
					}
				}
			}
		}
	}

	private static void printAlgorithmSummary(PrintStream out) {

		SummaryAlgorithmResultTable resultsTable = new SummaryAlgorithmResultTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser, CommonConfig.dbPass);) {

			for (AlgorithmExperiment experiment : SummaryDataAccess
					.selectFinishedAlgorithmExperiments(connection)) {
				SummaryAlgorithmResults results = resultsTable
						.selectResults(experiment.getExperimentCase());
				results.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printAlgorithmSummaryForAttribute(out, resultsTable,
				new SummaryAlgorithmResultAttributeSelector.FirstCost0());
		printAlgorithmSummaryForAttribute(out, resultsTable,
				new SummaryAlgorithmResultAttributeSelector.FirstCost1());
		printAlgorithmSummaryForAttribute(out, resultsTable,
				new SummaryAlgorithmResultAttributeSelector.FirstCost2());
		printAlgorithmSummaryForAttribute(out, resultsTable,
				new SummaryAlgorithmResultAttributeSelector.FirstCost3());
		printAlgorithmSummaryForAttribute(out, resultsTable,
				new SummaryAlgorithmResultAttributeSelector.SuccessCount());
	}

	public static double getConfidenceIntervalWidth(
			StatisticalSummary statistics, double significance) {
		TDistribution tDist = new TDistribution(statistics.getN() - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * statistics.getStandardDeviation()
				/ Math.sqrt(statistics.getN());
	}
}
