package impossible.tfind.xamcra;

import impossible.model.topology.EdgeDefinition;
import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Path;
import impossible.model.topology.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MamcraB {
	public Tree optimizePaths(Graph graph, Set<Path> paths, List<Node> destinations) {
		
		List<EdgeDefinition> resultDefinitions = new ArrayList<>();

		while (!paths.isEmpty()) {
			Path path = findWithMostMembers(paths, destinations);
			// TODO: Carry on with this.
		}

		return new Tree(graph, resultDefinitions);
	}

	private Path findWithMostMembers(Set<Path> paths, List<Node> destinations) {
		// TODO Auto-generated method stub
		return null;
	}
}
