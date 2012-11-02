package impossible.pivot.aggregators;


public class SumAggregator implements Aggregator {

	private final AggrName stronglyTypedName;
	private double sum;

	public SumAggregator() {
		this.stronglyTypedName = AggrName.SUM;
		sum = 0.0;
	}

	@Override
	public void put(double value) {
		sum += value;
	}

	@Override
	public double get() {
		return sum;
	}

	@Override
	public String getName() {
		return stronglyTypedName.toString();
	}

}
