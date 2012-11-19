package impossible.tfind.tabu;

import java.util.ArrayList;
import java.util.List;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;

public class ShortTermTabuSearch implements MetricConstrainedSteinerTreeFinder {

	private final MoveProvider moveProvider;

	public ShortTermTabuSearch(MoveProvider moveProvider) {
		this.moveProvider = moveProvider;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {

		// Configuration.
		final int maxIterations = 100;
		final double aspirationLevel = 100.0;

		// State.
		List<Move> tabuList = new ArrayList<>();
		int iteration = 0;

		// Result candidates.
		Tree solution = moveProvider.getInitialSolution(graph, group);
		Tree best = solution;

		// Optimization loop.
		while (iteration < maxIterations) {

			Move bestMove = moveProvider.getBestNeighborMove(graph, group);

			boolean moveInTabu = tabuList.contains(bestMove);
			boolean goodAspiration = solution.getMetrics().get(0) < aspirationLevel;

			if (!moveInTabu || goodAspiration) {

				tabuList.add(bestMove);
				solution = moveProvider.performPreviousBestMove(graph, group);
				++iteration;
				if (solution.getMetrics().get(0) < best.getMetrics().get(0)) {
					best = solution;
				}
			}

		}
		
		return best;
	}

}
