package pfnd;


import helpers.ConstraintsComparer;

import java.util.List;

import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.PathSubstiutor;


public interface PathFinderFactory {

	PathFinder createDijkstra(Relaxation commonRelaxation);

	PathFinder createDijkstraIndex(int metricIndex);

	PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas);

	ConstrainedPathFinder createHmcp();

	ConstrainedPathFinder createMlarac(PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer);

	ConstrainedPathFinder createLbpsa(ConstraintsComparer constraintsComparer);
	
	ConstrainedPathFinder createHmcop(double lambda);
}
