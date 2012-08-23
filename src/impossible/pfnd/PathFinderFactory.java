package impossible.pfnd;

import impossible.helpers.ConstraintsComparer;
import impossible.pfnd.dkstr.DijkstraRelaxation;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;

import java.util.List;

public interface PathFinderFactory {

	PathFinder createDijkstra(DijkstraRelaxation dijkstraRelaxation);

	PathFinder createDijkstraIndex(int m);

	PathFinder createLinearCombinationDijkstra(int offset,
			List<Double> constraints, List<Double> lambdas);

	PathFinder createHmcp(List<Double> constraints);

	PathFinder createMlarac(List<Double> constraints,
			PathSubstiutor pathSubstitutor, LambdaEstimator lambdaEstimator,
			ConstraintsComparer constraintsComparer);

}
