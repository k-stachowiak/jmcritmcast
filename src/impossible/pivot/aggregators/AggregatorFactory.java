package impossible.pivot.aggregators;

public interface AggregatorFactory {

	public abstract Aggregator createFromName(AggrName stronglyTypedName);

}