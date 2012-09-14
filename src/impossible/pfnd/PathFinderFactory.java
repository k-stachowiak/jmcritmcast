package impossible.pfnd;

import impossible.helpers.ConstraintsComparer;
import impossible.pfnd.dkstr.DijkstraRelaxation;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;

import java.util.List;

public interface PathFinderFactory {

	PathFinder createDijkstra(DijkstraRelaxation dijkstraRelaxation);

	PathFinder createDijkstraIndex(int m);

	PathFinder createLinearCombinationDijkstra(int offset, List<Double> lambdas);

	ConstrainedPathFinder createHmcp();

	ConstrainedPathFinder createMlarac(PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer);

	ConstrainedPathFinder createLbpsa(ConstraintsComparer constraintsComparer);
	
	ConstrainedPathFinder createHmcop(double lambda);
}
