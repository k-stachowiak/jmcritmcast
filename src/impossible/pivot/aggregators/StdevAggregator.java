package impossible.pivot.aggregators;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;


public class StdevAggregator implements Aggregator {
	
	private final AggrName stronglyTypedName;
	
	SummaryStatistics stat;
	
	public StdevAggregator() {
		stronglyTypedName = AggrName.STDEV;
		stat = new SummaryStatistics();
	}

	@Override
	public void put(double value) {
		stat.addValue(value);		
	}

	@Override
	public double get() {
		return stat.getStandardDeviation();
	}
	
	@Override
	public String getName() {
		return stronglyTypedName.toString();
	}

}
