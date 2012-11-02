package impossible.pivot.aggregators;


public class AggregatorFactoryImpl implements AggregatorFactory {

	private final double alpha;

	public AggregatorFactoryImpl(double confIntAlpha) {
		this.alpha = confIntAlpha;
	}

	@Override
	public Aggregator createFromName(AggrName stronglyTypedName) {

		switch (stronglyTypedName) {
		case CONF_INT:
			return new ConfidenceIntervalAggregator(alpha);
			
		case COUNT:
			return new CountAggregator();

		case MEAN:
			return new MeanAggregator();

		case STDEV:
			return new StdevAggregator();

		case SUM:
			return new SumAggregator();

		default:
			throw new RuntimeException("Aggregator of unknown name requested.");
		}
	}

}
