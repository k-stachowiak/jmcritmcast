package impossible.tfind.tabu;

import impossible.helpers.gphmut.NodeRemover;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.SubGraph;
import impossible.model.topology.SubGraphToGraphAdapter;
import impossible.model.topology.Tree;
import impossible.tfind.SteinerTreeFinder;
import impossible.tfind.generalprim.CheapestNonBreakingEdgeSelector;
import impossible.tfind.generalprim.EdgeSelector;
import impossible.tfind.generalprim.GeneralPrimTreeFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SteinerNodeMoveProvider implements MoveProvider {

	private final SteinerTreeFinder minDelayFinder;
	private final NodeRemover nodeRemover;
	private final List<Double> constraints;

	private Tree currentSolution;
	private Map<Node, Boolean> currentSolutionCode;
	private Map<Node, Boolean> previousBestCode;

	public SteinerNodeMoveProvider(SteinerTreeFinder minDelayFinder,
			NodeRemover nodeRemover, List<Double> constraints) {
		this.minDelayFinder = minDelayFinder;
		this.nodeRemover = nodeRemover;
		this.constraints = constraints;
		previousBestCode = null;
	}

	@Override
	public Tree getInitialSolution(Graph graph, List<Node> spanned) {
		return minDelayFinder.find(graph, spanned);
	}

	@Override
	public Move getBestNeighborMove(Graph graph, List<Node> spanned) {

		Double cheapestCost = Double.POSITIVE_INFINITY;
		Move bestMove = null;
		Map<Node, Boolean> bestCode = null;

		// Iterate over all the possible moves.
		for (Node n : currentSolutionCode.keySet()) {

			// Generate new solution code.
			Map<Node, Boolean> newCode = new HashMap<>(currentSolutionCode);
			newCode.put(n, !currentSolutionCode.get(n));

			// Generate a tree from the code.
			Tree candidate = computeSolutionFromCode(newCode, graph, spanned);
			double cost = candidate.getMetrics().get(0);

			if (cost < cheapestCost) {
				cheapestCost = cost;
				bestMove = new SteinerNodeMove(newCode);
				bestCode = newCode;
			}
		}

		previousBestCode = bestCode;
		return bestMove;
	}

	@Override
	public Tree performPreviousBestMove(Graph graph, List<Node> spanned) {
		currentSolutionCode = previousBestCode;
		currentSolution = computeSolutionFromCode(previousBestCode, graph,
				spanned);
		return currentSolution;
	}

	// Builds a map assigning each of the original graph nodes a flag
	// indicating whether it is a Steiner node or not. The definition of
	// the Steiner node is that it belongs to the result tree, but is neither
	// the source node nor any of the destinations.
	private Map<Node, Boolean> computeCodeFromSolution(Tree solution,
			List<Node> spanned) {

		Map<Node, Boolean> code = new HashMap<>();
		for (Node n : solution.getParent().getNodes()) {
			boolean isSteiner = solution.getNodes().contains(n.getId())
					&& !spanned.contains(n.getId());
			code.put(n, isSteiner);
		}
		return code;
	}

	// Builds the resulting tree by removing from the original graph all the
	// nodes that aren't the Steiner nodes or any of the spanned nodes.
	// Spanned nodes are all the nodes from the sum of the set of the receivers
	// and the one element set containing the source node.
	// Removing a node also implies removing all of its adjacent edges.
	private Tree computeSolutionFromCode(Map<Node, Boolean> code, Graph graph,
			List<Node> spanned) {

		// Prepare the stripped down graph.
		// --------------------------------
		List<Node> obsoleteNodes = new ArrayList<>();
		for (Node n : graph.getNodes()) {
			if (!spanned.contains(n) && !code.containsKey(n)) {
				obsoleteNodes.add(n);
			}
		}

		SubGraph strippedSubGraph = nodeRemover.removeNodes(graph,
				obsoleteNodes);

		Graph strippedGraph = new SubGraphToGraphAdapter(strippedSubGraph);

		// Perform the modified spanning tree find.
		// ----------------------------------------
		EdgeSelector edgeSelector = new CheapestNonBreakingEdgeSelector(
				constraints);

		GeneralPrimTreeFinder primFinder = new GeneralPrimTreeFinder(
				edgeSelector);

		Tree result = primFinder.find(spanned.get(0), strippedGraph);

		currentSolution = result;
		currentSolutionCode = computeCodeFromSolution(result, spanned);

		return result;
	}
}
