package edu.put.et.stik.mm.pfnd;


import java.util.List;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.pfnd.mlarac.LambdaEstimator;
import edu.put.et.stik.mm.pfnd.mlarac.PathSubstiutor;

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
