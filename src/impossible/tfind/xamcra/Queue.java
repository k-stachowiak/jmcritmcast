package impossible.tfind.xamcra;

import impossible.model.topology.Node;

import java.util.Map;
import java.util.TreeMap;

public class Queue {

	private final TreeMap<Double, PathNode> storage;
	
	// Intended for tests only.
	Queue(TreeMap<Double, PathNode> injectedStorage) {
		storage = injectedStorage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<Double, PathNode> entry : storage.entrySet()) {
			sb.append(entry.getKey() + " -> " + storage + "\n");
		}
		return sb.toString();
	}

	public Queue() {
		storage = new TreeMap<>();
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public void push(double label, PathNode pathNode) {
		storage.put(label, pathNode);
	}

	public PathNode pop() {
		Map.Entry<Double, PathNode> first = storage.firstEntry();
		storage.remove(first.getKey());
		return first.getValue();
	}

	public PathNode findMaxTo(Node node) {
		
		// Find farthest.
		double farthestKey = -1.0;
		for(Map.Entry<Double, PathNode> entry : storage.entrySet()) {
			
			// Only consider paths to node.
			if(!node.equals(entry.getValue().getNode())) {
				continue;
			}
			
			if (entry.getKey() > farthestKey) {
				farthestKey = entry.getKey();
			}
		}

		// Return the cheapest.
		return storage.get(farthestKey);
	}

	public void replace(PathNode oldPath, PathNode newPath) {
		for(Map.Entry<Double, PathNode> entry : storage.entrySet()) {
			if(entry.getValue().equals(oldPath)) {
				storage.put(entry.getKey(), newPath);
			}
		}
	}
}
