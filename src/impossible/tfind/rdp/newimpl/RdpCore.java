package impossible.tfind.rdp.newimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import impossible.model.topology.Graph;
import impossible.model.topology.Node;
import impossible.model.topology.Tree;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;

public class RdpCore implements MetricConstrainedSteinerTreeFinder {

	private final Variant variant;

	public RdpCore(Variant variant) {
		this.variant = variant;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {

		// Initialize state.
		Map<Node, Integer> signalMap = new HashMap<>();
		List<ConvergenceProcess> procs = new ArrayList<>();
		for (Node member : group) {
			procs.add(variant.initConvergenceProcess(graph, member));
			signalMap.put(member, 0);
		}

		boolean allDone = false;
		while (!allDone) {

			// Select soonest.
			ConvergenceProcess soonestProc = null;
			double soonestTime = Double.POSITIVE_INFINITY;
			for (ConvergenceProcess rp : procs) {
				if (rp.nextEventTime() < soonestTime) {
					soonestTime = rp.nextEventTime();
					soonestProc = rp;
				}
			}

			// Increase counter.
			Node visited = soonestProc.handleNextEvent();
			signalMap.put(visited, signalMap.get(visited) + 1);

			// Detect RDP candidate.
			if (signalMap.get(visited) == group.size()) {
				
				ResultBuildProcess resultBuildProcess = variant
						.initResultBuildProcess(graph, visited);
				
				Tree result;
				while ((result = resultBuildProcess.tryNext()) != null) {
					if (result != null) {
						return result;
					}
				}
			}

			// End condition.
			allDone = true;
			for (ConvergenceProcess proc : procs) {
				if (!proc.isDone()) {
					allDone = false;
					break;
				}
			}
		}

		// Failure.
		return null;
	}

}
