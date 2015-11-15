package apps.analsum;

import java.io.File;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;

public abstract class SummaryUtils {

	public static double getConfidenceIntervalWidth(double n, double stdev, double significance) {
		TDistribution tDist = new TDistribution(n - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
		return a * stdev / Math.sqrt(n);
	}

	public static double getConfidenceIntervalWidth(StatisticalSummary statistics, double significance) {
		return getConfidenceIntervalWidth(statistics.getN(), statistics.getStandardDeviation(), significance);
	}

	public static void createDirIfNotExists(String name) {
		File dir = new File(name);
		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (SecurityException ex) {
				ex.printStackTrace();
			}
		}
	}

}
