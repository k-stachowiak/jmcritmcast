package impossible.tfind.tabu;

import java.util.List;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;

public interface MoveProvider {

	Tree getInitialSolution(Graph graph, List<Node> spanned);

	Move getBestNeighborMove(Graph graph, List<Node> spanned);

	Tree performPreviousBestMove(Graph graph, List<Node> spanned);

}
