package impossible.tfind;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.PathAggregator;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;

import java.util.List;

public interface TreeFinderFactory {
	ConstrainedSteinerTreeFinder createConstrainedPathAggr(List<Double> constraints,
			ConstrainedPathFinder pathFinder,
			ConstraintsComparer constraintsComparer,
			PathAggregator pathAggregator);

	ConstrainedSteinerTreeFinder createHmcmc(ConstraintsComparer constraintsComparer,
			PathFinderFactory pathFinderFactory, PathAggregator pathAggregator,
			List<Double> constraints);

	SpanningTreeFinder createPrim(MetricProvider metricProvider);
}
