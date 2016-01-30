package apps.analsum.cstr;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryConstraintResult {
	
	private final SummaryStatistics minStats = new SummaryStatistics();
	private final SummaryStatistics maxStats = new SummaryStatistics();

	public long getN() {
		// Note it must be secured that the N is the same for all stored stats.
		return minStats.getN();
	}

	public SummaryStatistics getMin() {
		return minStats;
	}

	public SummaryStatistics getMax() {
		return maxStats;
	}

	public void put(double min, double max) {
		minStats.addValue(min);
		maxStats.addValue(max);
	}

}
