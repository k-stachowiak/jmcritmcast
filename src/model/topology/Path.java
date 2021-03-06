package model.topology;

import java.util.ArrayList;
import java.util.List;

public class Path extends SubGraph {

	public Path(Graph parent, List<Integer> nodes) {
		super(parent, nodes, deriveEdgesFromNodes(nodes));
	}

	public Path clone() {
		return new Path(parent, new ArrayList<>(getNodes()));
	}

	private static List<EdgeDefinition> deriveEdgesFromNodes(List<Integer> nodes) {
		List<EdgeDefinition> result = new ArrayList<>();
		for (int i = 1; i < nodes.size(); ++i)
			result.add(new EdgeDefinition(nodes.get(i - 1), nodes.get(i)));
		return result;
	}
}
