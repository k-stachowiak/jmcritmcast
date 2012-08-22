package impossible.helpers.nodegrp;

import impossible.model.Graph;
import impossible.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomNodeGroupper implements NodeGroupper {

	private final Random random;

	public RandomNodeGroupper(Random random) {
		super();
		this.random = random;
	}

	@Override
	public List<Node> group(Graph graph, int groupSize) {
		List<Node> result = new ArrayList<>();
		for (int n = 0; n < groupSize; ++n) {
			Node candidate;
			do {
				List<Node> nodes = graph.getNodes();
				
				int index = random.nextInt(nodes.size());				
				candidate = nodes.get(index);

			} while (result.contains(candidate));
			result.add(candidate);
		}
		return result;
	}

}
