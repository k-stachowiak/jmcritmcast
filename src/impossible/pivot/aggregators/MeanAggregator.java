package impossible.pivot.aggregators;


public class MeanAggregator implements Aggregator {
	
	private final AggrName stronglyTypedName;
	private double sum;
	private int count;
	
	public MeanAggregator() {
		this.stronglyTypedName = AggrName.MEAN;
		sum = 0.0;
		count = 0;
	}

	@Override
	public void put(double value) {
		sum += value;
		++count;
	}

	@Override
	public double get() {
		return sum / (double)count;
	}
	
	@Override
	public String getName() {
		return stronglyTypedName.toString();
	}

}
