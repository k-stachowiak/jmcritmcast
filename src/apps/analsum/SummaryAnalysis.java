package apps.analsum;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apps.CommonConfig;
import apps.alganal.AlgorithmExperiment;
import apps.analsum.algo.SummaryAlgorithmReportGnuplot;
import apps.analsum.algo.SummaryAlgorithmReportPrintStream;
import apps.analsum.algo.SummaryAlgorithmResultAttributeSelector;
import apps.analsum.algo.SummaryAlgorithmResultTable;
import apps.analsum.cstr.SummaryConstraintGroupReportGnuplot;
import apps.analsum.cstr.SummaryConstraintNodeReportGnuplot;
import apps.analsum.cstr.SummaryConstraintNodeReportPrintStream;
import apps.analsum.group.SummaryGroupReportGraphSizeGnuplot;
import apps.analsum.group.SummaryGroupReportGroupSizePrintStream;
import apps.analsum.group.SummaryGroupResultAttributeSelector;
import apps.analsum.group.SummaryGroupResultGraphSizeTable;
import apps.analsum.group.SummaryGroupResultGroupSizeTable;
import apps.analsum.top.SummaryTopologyReportGnuplot;
import apps.analsum.top.SummaryTopologyReportPrintStream;
import apps.analsum.top.SummaryTopologyResultAttributeSelector;
import apps.analsum.top.SummaryTopologyResultTable;
import apps.groupanal.GroupExperiment;
import apps.topanal.TopologyExperiment;
import dal.TopologyDAO;
import dal.TopologyDAO.EdgeStatistic;
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
			// printMetricsSummary(System.out);
			printConstraintsSummary(System.out);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void printConstraintsSummary(PrintStream out) {
		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass);) {
			printConstraintsSummary(connection, "Degree", out);
			printConstraintsSummary(connection, "Centroid", out);
			printConstraintsSummary(connection, "Random", out);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
	}

	private static void printConstraintsSummary(Connection connection,
			String groupperName, PrintStream out) {
		for (int nodesCount : CommonConfig.nodesCounts) {
			printConstraintsSummaryNodes(connection, groupperName, nodesCount,
					1, out);
			printConstraintsSummaryNodes(connection, groupperName, nodesCount,
					2, out);
		}
		for (int groupSize : CommonConfig.groupSizes) {
			printConstraintsSummaryGroup(connection, groupperName, groupSize, 1,
					out);
			printConstraintsSummaryGroup(connection, groupperName, groupSize, 2,
					out);
		}
	}

	private static void printConstraintsSummaryNodes(Connection connection,
			String groupperName, int nodesCount, int metricIndex,
			PrintStream out) {
		(new SummaryConstraintNodeReportGnuplot()).perform(connection,
				groupperName, nodesCount, metricIndex);
		(new SummaryConstraintNodeReportPrintStream(out)).perform(connection,
				groupperName, nodesCount, metricIndex);
	}

	private static void printConstraintsSummaryGroup(Connection connection,
			String groupperName, int groupSize, int metricIndex,
			PrintStream out) {
		(new SummaryConstraintGroupReportGnuplot()).perform(connection,
				groupperName, groupSize, metricIndex);
	}

	private static void printMetricsSummary(PrintStream out) {
		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass);) {

			TopologyDAO topologyDao = new TopologyDAO(connection);

			EdgeStatistic statisticBarabasi50 = topologyDao
					.selectStatistics(TopologyType.ASBarabasi, 50, 0.1);
			Map<Double, Integer> histogramBarabasi50 = statisticBarabasi50
					.getMetric1().getHistogram();
			printHistogram(histogramBarabasi50);

			EdgeStatistic statisticWaxman50 = topologyDao
					.selectStatistics(TopologyType.ASWaxman, 50, 0.1);
			Map<Double, Integer> histogramWaxman50 = statisticWaxman50
					.getMetric1().getHistogram();
			printHistogram(histogramWaxman50);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}
	}

	private static void printHistogram(Map<Double, Integer> histogram) {
		Iterator<Entry<Double, Integer>> iterator = histogram.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<Double, Integer> entry = iterator.next();
			System.out.printf("%f\t%d\n", entry.getKey(), entry.getValue());
		}
	}

	private static void printGroupSummaryForAttribute(PrintStream out,
			SummaryGroupResultGroupSizeTable groupResultsTable,
			SummaryGroupResultGraphSizeTable graphResultsTable,
			SummaryGroupResultAttributeSelector attributeSelector) {
		(new SummaryGroupReportGroupSizePrintStream(out))
				.perform(groupResultsTable, attributeSelector);
		(new SummaryGroupReportGraphSizeGnuplot()).perform(graphResultsTable,
				attributeSelector);
	}

	private static void printGroupSummary(PrintStream out) {

		SummaryGroupResultGroupSizeTable gropuResultsTable = new SummaryGroupResultGroupSizeTable();
		SummaryGroupResultGraphSizeTable graphResultsTable = new SummaryGroupResultGraphSizeTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass);) {

			for (GroupExperiment experiment : SummaryDataAccess
					.selectFinishedGroupExperiments(connection)) {
				gropuResultsTable.selectResults(experiment.getExperimentCase())
						.insert(experiment.getExperimentValues());
				graphResultsTable.selectResults(experiment.getExperimentCase())
						.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printGroupSummaryForAttribute(out, gropuResultsTable, graphResultsTable,
				new SummaryGroupResultAttributeSelector.Degree());
		printGroupSummaryForAttribute(out, gropuResultsTable, graphResultsTable,
				new SummaryGroupResultAttributeSelector.DiameterHop());
		printGroupSummaryForAttribute(out, gropuResultsTable, graphResultsTable,
				new SummaryGroupResultAttributeSelector.DiameterCost());
		printGroupSummaryForAttribute(out, gropuResultsTable, graphResultsTable,
				new SummaryGroupResultAttributeSelector.ClusteringCoefficient());
		printGroupSummaryForAttribute(out, gropuResultsTable, graphResultsTable,
				new SummaryGroupResultAttributeSelector.Density());
	}

	private static void printTopologySummaryForAttribute(PrintStream out,
			SummaryTopologyResultTable resultsTable,
			SummaryTopologyResultAttributeSelector attributeSelector) {
		(new SummaryTopologyReportPrintStream(out)).perform(resultsTable,
				attributeSelector);
		(new SummaryTopologyReportGnuplot()).perform(resultsTable,
				attributeSelector);
	}

	private static void printTopologySummary(PrintStream out) {

		SummaryTopologyResultTable resultsTable = new SummaryTopologyResultTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass);) {

			for (TopologyExperiment experiment : SummaryDataAccess
					.selectFinishedTopologyExperiments(connection)) {
				resultsTable.selectResults(experiment.getExperimentCase())
						.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelector.Degree());
		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelector.DiameterHop());
		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelector.DiameterCost());
		printTopologySummaryForAttribute(out, resultsTable,
				new SummaryTopologyResultAttributeSelector.ClusteringCoefficient());
	}

	private static void printAlgorithmSummaryForAttributes(PrintStream out,
			SummaryAlgorithmResultTable resultsTable,
			List<SummaryAlgorithmResultAttributeSelector> attributeSelectors) {
		(new SummaryAlgorithmReportPrintStream(out)).perform(resultsTable,
				attributeSelectors);
		(new SummaryAlgorithmReportGnuplot()).perform(resultsTable,
				attributeSelectors);
	}

	private static void printAlgorithmSummary(PrintStream out) {

		SummaryAlgorithmResultTable resultsTable = new SummaryAlgorithmResultTable();

		try (Connection connection = DriverManager.getConnection(
				CommonConfig.dbUri, CommonConfig.dbUser,
				CommonConfig.dbPass);) {

			for (AlgorithmExperiment experiment : SummaryDataAccess
					.selectFinishedAlgorithmExperiments(connection)) {
				resultsTable.selectResults(experiment.getExperimentCase())
						.insert(experiment.getExperimentValues());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			logger.fatal("Sql error: {}", e.getMessage());
		}

		printAlgorithmSummaryForAttributes(out, resultsTable, Arrays.<SummaryAlgorithmResultAttributeSelector>asList(
				new SummaryAlgorithmResultAttributeSelector.FirstCost0()));

		printAlgorithmSummaryForAttributes(out, resultsTable, Arrays.<SummaryAlgorithmResultAttributeSelector>asList(
				new SummaryAlgorithmResultAttributeSelector.FirstCost1()));

		printAlgorithmSummaryForAttributes(out, resultsTable, Arrays.<SummaryAlgorithmResultAttributeSelector>asList(
				new SummaryAlgorithmResultAttributeSelector.FirstCost2()));

		printAlgorithmSummaryForAttributes(out, resultsTable, Arrays.<SummaryAlgorithmResultAttributeSelector>asList(
				new SummaryAlgorithmResultAttributeSelector.SuccessCount()));
	}

	public static double getConfidenceIntervalWidth(double n, double stdev,
			double significance) {
		TDistribution tDist = new TDistribution(n - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * stdev / Math.sqrt(n);
	}

	public static double getConfidenceIntervalWidth(
			StatisticalSummary statistics, double significance) {
		return getConfidenceIntervalWidth(statistics.getN(),
				statistics.getStandardDeviation(), significance);
	}
}
