package impossible.tfind.xamcra;

import java.util.ArrayList;
import java.util.List;

import impossible.model.topology.Node;

public class Queue {

	private List<PathNode> storage;

	Queue() {
		storage = new ArrayList<>();
	}

	public boolean queueEmpty() {
		return storage.isEmpty();
	}

	public void push(PathNode pathNode) {
		storage.add(pathNode);
	}

	public PathNode pop() {

		// Find cheapest.
		int cheapestIndex = -1;
		double cheapestCost = Double.POSITIVE_INFINITY;
		for (int i = 0; i < storage.size(); ++i) {
			PathNode pNode = storage.get(i);
			if (pNode.label < cheapestCost) {
				cheapestIndex = i;
				cheapestCost = pNode.label;
			}
		}

		// Remove cheapest.
		PathNode cheapestPNode = storage.get(cheapestIndex);
		storage.remove(cheapestIndex);

		// Return the cheapest.
		return cheapestPNode;
	}

	public PathNode popMaxTo(Node node) {
		
		// Find farthest.
		int farthestIndex = -1;
		double greatestCost = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < storage.size(); ++i) {
			
			PathNode pNode = storage.get(i);
			
			// Only consider paths to node.
			if(!node.equals(pNode.node)) {
				continue;
			}
			
			if (pNode.label >  greatestCost) {
				farthestIndex = i;
				greatestCost = pNode.label;
			}
		}

		// Remove cheapest.
		PathNode farthestPNode = storage.get(farthestIndex);
		storage.remove(farthestIndex);

		// Return the cheapest.
		return farthestPNode;
	}

	public void queueReplace(PathNode oldPath, PathNode newPath) {
		for(int i = 0; i < storage.size(); ++i) {
			if(storage.get(i).equals(oldPath)) {
				storage.set(i, newPath);
				return;
			}
		}
	}
}
