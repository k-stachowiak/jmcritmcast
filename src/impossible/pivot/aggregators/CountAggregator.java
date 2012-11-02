package impossible.pivot.aggregators;


public class CountAggregator implements Aggregator {

	private final AggrName stronglyTypedName;
	private int count;

	public CountAggregator() {
		this.stronglyTypedName = AggrName.COUNT;
		count = 0;
	}

	@Override
	public void put(double value) {
		++count;
	}

	@Override
	public double get() {
		return (double)count;
	}
	
	@Override
	public String getName() {		
		return stronglyTypedName.toString();
	}

}
