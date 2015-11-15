package helpers;

import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class ExtendedStatistics {

	private final SummaryStatistics apache;
	private final Histogram histogram;

	public ExtendedStatistics(double bucketSize) {
		this.apache = new SummaryStatistics();
		this.histogram = new Histogram(bucketSize);
	}

	public void put(double value) {
		apache.addValue(value);
		histogram.put(value);
	}

	public double getN() {
		return apache.getN();
	}

	public double getMean() {
		return apache.getMean();
	}

	public double getStandardDeviation() {
		return apache.getStandardDeviation();
	}

	public Map<Double, Integer> getHistogram() {
		return histogram.get();
	}
}
