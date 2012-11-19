package impossible.tfind.generalprim;

import impossible.model.topology.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CheapestNonBreakingEdgeSelector implements EdgeSelector {
	
	private final List<Double> constraints;
	private Map<Integer, List<Double>> accumulatedMetrics;
	private boolean handledRoot;

	public CheapestNonBreakingEdgeSelector(List<Double> constraints) {
		this.constraints = constraints;
	}

	@Override
	public void reset() {
		accumulatedMetrics = new TreeMap<>();
		handledRoot = false;
	}

	@Override
	public Edge select(List<Edge> cutEdges) {		
		
		double cheapestCost = Double.POSITIVE_INFINITY;
		Edge cheapestValid = null;
		
		for(Edge e : cutEdges) {
			
			// This is only legal for the first expansion as the tree root
			// has not yet been considered.
			if(!accumulatedMetrics.containsKey(e.getFrom())) {
				
				// Allow this only once.
				if(!handledRoot) {
					handledRoot = true;					
					List<Double> zeros = new ArrayList<>();
					for(int m = 1; m < e.getMetrics().size(); ++m) {
						zeros.add(0.0);
					}
					accumulatedMetrics.put(e.getFrom(), zeros);
				} else {
					// This may not happen more than once.
					throw new RuntimeException("Prim expansion inconsistency.");
				}
			}
			
			// Add the destination node to the record.
			List<Double> soFar = accumulatedMetrics.get(e.getFrom());
			List<Double> dest = new ArrayList<>();
			boolean breaksConstraints = false;
			for(int m = 1; m < e.getMetrics().size(); ++m) {
				double newMetric = soFar.get(m - 1) + e.getMetrics().get(m);
				dest.add(newMetric);
				if(newMetric > constraints.get(m - 1)) {
					breaksConstraints = true;
				}
			}
			
			// Verify the validity and optimality.
			double cost = e.getMetrics().get(0);
			if(!breaksConstraints && cost < cheapestCost) {
				cheapestCost = cost;
				cheapestValid = e;
				
				// It only makes sense to store the records for the valid edges.
				accumulatedMetrics.put(e.getTo(), dest);
			}
				
		}
		
		return cheapestValid;
	}
	
}
