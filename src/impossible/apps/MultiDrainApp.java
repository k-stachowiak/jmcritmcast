package impossible.apps;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.hlanalysis.MultiDrain;
import impossible.hlanalysis.MultiDrainSetup;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import impossible.pfnd.mlarac.IntersectLambdaEstimator;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;
import impossible.tfind.ConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiDrainApp {

	public static void main(String[] args) {

		final long randomSeed = 1L;
		final double fengDelta = 0.9;
		final double baseBandwidth = 10000.0;
		final double drainedBandwidth = 100.0;

		final String topologiesDirecotry = "data/new";
		final String topology = "ASBarabasi";
		final int graphsInFile = 1000;

		final double redistributionMin = 1;
		final double redistributionMax = 1000;

		final int graphs = 5;

		final List<Integer> nodeSizes = new ArrayList<>();
		nodeSizes.add(50);

		final List<Integer> criteriaCounts = new ArrayList<>();
		criteriaCounts.add(2);
		criteriaCounts.add(3);

		final List<Integer> groupSizes = new ArrayList<>();
		groupSizes.add(5);
		groupSizes.add(10);

		Map<String, ConstrainedSteinerTreeFinder> treeFinders = initializeTreeFinders();

		final MultiDrainSetup setup = new MultiDrainSetup(randomSeed,
				fengDelta, baseBandwidth, drainedBandwidth, graphs, nodeSizes,
				criteriaCounts, groupSizes, topologiesDirecotry, topology,
				graphsInFile, redistributionMin, redistributionMax, treeFinders);

		new MultiDrain(setup).run(args);
	}

	private static Map<String, ConstrainedSteinerTreeFinder> initializeTreeFinders() {

		// Factories.
		// ----------
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Strategies.
		// -----------
		MetricProvider metricProvider = new IndexMetricProvider(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// Helper MLARAC path finder.
		// --------------------------
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();

		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();

		ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(null,
				pathSubstitutor, lambdaEstimator, constraintsComparer);

		ConstrainedPathFinder lbpsa = pathFinderFactory.createLbpsa(
				pathFinderFactory, constraintsComparer, null);

		// Build the result.
		// -----------------
		Map<String, ConstrainedSteinerTreeFinder> treeFinders = new HashMap<>();

		treeFinders.put("HMCMC", treeFinderFactory.createHmcmc(
				constraintsComparer, pathFinderFactory, pathAggregator, null));

		treeFinders.put("AGGR_MLARAC", treeFinderFactory
				.createConstrainedPathAggr(mlarac, pathAggregator));

		treeFinders.put("AGGR_LBPSA", treeFinderFactory
				.createConstrainedPathAggr(lbpsa, pathAggregator));

		return treeFinders;
	}

}
