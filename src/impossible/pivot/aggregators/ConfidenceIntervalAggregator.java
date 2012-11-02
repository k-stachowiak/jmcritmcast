package impossible.pivot.aggregators;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


public class ConfidenceIntervalAggregator implements Aggregator {

	private final AggrName stronglyTypedName;
	private final SummaryStatistics stat;
	private final double alpha;

	public ConfidenceIntervalAggregator(double alpha) {
		stronglyTypedName = AggrName.CONF_INT;
		stat = new SummaryStatistics();
		this.alpha = alpha;
	}

	@Override
	public void put(double value) {
		stat.addValue(value);
	}

	@Override
	public double get() {
		
		double mean = stat.getMean();
		double sd = stat.getStandardDeviation() / Math.sqrt(stat.getN());
		
		System.out.println("mean = " + mean);
		System.out.println("stdev = " + sd);
		
		NormalDistribution dist = new NormalDistribution(mean, sd);
		
		double leftProbability = (1.0 - alpha) * 0.5;
		double rightProbability = leftProbability + alpha;
		
		System.out.println("lower p = " + leftProbability);
		System.out.println("upper p = " + rightProbability);
		
		double leftLimit = dist.inverseCumulativeProbability(leftProbability);
		double rightLimit = dist.inverseCumulativeProbability(rightProbability);
		
		System.out.println("lower = " + leftLimit);
		System.out.println("upper = " + rightLimit);
		
		return rightLimit - leftLimit;
	}

	@Override
	public String getName() {
		return stronglyTypedName.toString();
	}

}
