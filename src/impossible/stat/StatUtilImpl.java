package impossible.stat;

import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class StatUtilImpl implements StatUtil {

	@Override
	public Interval confidenceInterval(List<Double> sample, double significance) {
		StatisticalSummary sampleSummary = collToStatSum(sample);
		return confidenceInterval(sampleSummary, significance);
	}

	@Override
	public double mean(List<Double> sample) {
		StatisticalSummary sampleSummary = collToStatSum(sample);
		return sampleSummary.getMean();
	}

	private Interval confidenceInterval(StatisticalSummary sampleSummary,
			double significance) {

		TDistribution tDist = new TDistribution(sampleSummary.getN() - 1);

		double a;
			a = tDist.inverseCumulativeProbability(1.0 - significance / 2);

		

		double width = a * sampleSummary.getStandardDeviation()
				/ Math.sqrt(sampleSummary.getN());

		return new Interval(sampleSummary.getMean() - width * 0.5,
				sampleSummary.getMean() + width * 0.5);
	}
	
	private StatisticalSummary collToStatSum(Collection<Double> collection) {
            
		SummaryStatistics sampleSummary = new SummaryStatistics();
                
		for (Double value : collection) {
			sampleSummary.addValue(value);
                }
                
		return sampleSummary;
	}

}
