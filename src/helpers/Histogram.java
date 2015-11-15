package helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Histogram {

	private final double bucketSize;
	private final HashMap<Double, Integer> buckets;

	public Histogram(double bucketSize) {
		this.bucketSize = bucketSize;
		buckets = new HashMap<>();
	}

	public void put(double value) {
		double scaled = value / bucketSize;
		double scaledShifted = scaled + 0.5;
		double bucketKey = Math.floor(scaledShifted);
		if (buckets.containsKey(bucketKey)) {
			buckets.put(bucketKey, buckets.get(bucketKey) + 1);
		} else {
			buckets.put(bucketKey, 1);
		}
	}

	public Map<Double, Integer> get() {
		TreeMap<Double, Integer> result = new TreeMap<>();
		Iterator<Entry<Double, Integer>> iterator = buckets.entrySet().iterator();

		double prevKey = Double.POSITIVE_INFINITY;
		while (iterator.hasNext()) {
			Entry<Double, Integer> entry = iterator.next();
			double bucketKey = entry.getKey();
			for (double i = prevKey + 1; i < bucketKey; i += 1.0) {
				result.put(i * bucketSize, 0);
			}
			result.put(bucketKey, entry.getValue());
			prevKey = bucketKey;
		}
		return result;
	}
}
