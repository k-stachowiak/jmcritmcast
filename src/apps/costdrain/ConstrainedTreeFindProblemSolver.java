package apps.costdrain;

import helpers.ConstraintsComparer;
import helpers.PathAggregator;
import helpers.metrprov.IndexMetricProvider;
import helpers.metrprov.MetricProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pfnd.ConstrainedPathFinder;
import pfnd.PathFinderFactory;
import pfnd.PathFinderFactoryImpl;
import pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import pfnd.mlarac.IntersectLambdaEstimator;
import pfnd.mlarac.LambdaEstimator;
import pfnd.mlarac.PathSubstiutor;
import tfind.ConstrainedSteinerTreeFinder;
import tfind.SpanningTreeFinder;
import tfind.TreeFinderFactory;
import tfind.TreeFinderFactoryImpl;
import dto.ConstrainedTreeFindProblemDTO;
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.Tree;
import model.topology.Node;

public class ConstrainedTreeFindProblemSolver {
	
	private final static AdjacencyListFactory factory = new AdjacencyListFactory();
	private final static Map<String, ConstrainedSteinerTreeFinder> finders = allocateFinders();	
	
	public Tree solve(ConstrainedTreeFindProblemDTO problem) {
		final Graph graph = factory.createFromDTO(problem.getGraph());
		final ConstrainedSteinerTreeFinder finder = finders.get(problem.getFinderName());
		List<Node> group = new ArrayList<>();
		for (Integer id : problem.getGroup()) {
			group.add(graph.getNode(id));
		}
		return finder.find(graph, group, problem.getConstraints());
	}

	private static Map<String, ConstrainedSteinerTreeFinder> allocateFinders() {

		// Factories.
		// ----------
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Strategies.
		// -----------
		MetricProvider metricProvider = new IndexMetricProvider(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		ConstraintsComparer constraintsComparer = new ConstraintsComparer();

		PathAggregator pathAggregator = new PathAggregator(
				spanningTreeFinder);

		// MLARAC path finder.
		// -------------------
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();

		ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(
				pathSubstitutor, lambdaEstimator, constraintsComparer);

		// LBPSA path finder.
		// ------------------
		ConstrainedPathFinder lbpsa = pathFinderFactory
				.createLbpsa(constraintsComparer);

		// HMCOP path finder.
		// ------------------
		double lambda = Double.POSITIVE_INFINITY;
		ConstrainedPathFinder hmcop = pathFinderFactory.createHmcop(lambda);

		// Build the result.
		// -----------------
		Map<String, ConstrainedSteinerTreeFinder> treeFinders = new HashMap<>();

		treeFinders.put("HMCMC", treeFinderFactory.createHmcmc(
				constraintsComparer, pathFinderFactory, pathAggregator));

		treeFinders.put("AGGR_MLARAC", treeFinderFactory
				.createConstrainedPathAggr(mlarac, pathAggregator));

		treeFinders.put("AGGR_LBPSA", treeFinderFactory
				.createConstrainedPathAggr(lbpsa, pathAggregator));

		treeFinders.put("AGGR_HMCOP", treeFinderFactory
				.createConstrainedPathAggr(hmcop, pathAggregator));

		treeFinders.put("RDP_QE",
				treeFinderFactory.createRdpQuasiExact(constraintsComparer));

		treeFinders.put("RDP_H",
				treeFinderFactory.createRdpHeuristic(constraintsComparer));

		return treeFinders;
	}
}
