package impossible.pfnd;

import impossible.helpers.ConstraintsComparer;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;

import java.util.List;

public interface PathFinderFactory {

	PathFinder createDijkstra(CommonRelaxation commonRelaxation);

	PathFinder createDijkstraIndex(int metricIndex);

	PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas);

	ConstrainedPathFinder createHmcp();

	ConstrainedPathFinder createMlarac(PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer);

	ConstrainedPathFinder createLbpsa(ConstraintsComparer constraintsComparer);
	
	ConstrainedPathFinder createHmcop(double lambda);
}
