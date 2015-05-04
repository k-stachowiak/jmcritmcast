package apps.analsum;

import java.io.PrintStream;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public class AnalysisSummary {

	public static void main(String[] args) {
		try {
			Class.forName("org.postgresql.Driver");
			printTopologySummary(System.out);
			printGroupSummary(System.out);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void printGroupSummary(PrintStream out) {
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
	}

	private static void printTopologySummary(PrintStream out) {
		
		/* 
		 * Expected data:
		 * N	top1	top2	...
		 * 50	attr	attr	...
		 * 150  attr	attr	...
		 * ...	...		...		...
		 */
	}

	public static double getConfidenceIntervalWidth(
			StatisticalSummary statistics, double significance) {
		TDistribution tDist = new TDistribution(statistics.getN() - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * statistics.getStandardDeviation()
				/ Math.sqrt(statistics.getN());
	}
}
