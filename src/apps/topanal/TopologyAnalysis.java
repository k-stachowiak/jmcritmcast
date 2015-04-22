package apps.topanal;

import java.util.Arrays;
import java.util.List;

public class TopologyAnalysis implements TopologyAnalysisExecutor {

	private static final List<Integer> nodesCounts = Arrays
			.asList(new Integer[] { 50, 100, 150, 200, 300 });

	private static void forEachCase(TopologyAnalysisExecutor executor) {
		for (TopologyType type : TopologyType.values()) {
			for (Integer nodesCount : nodesCounts) {
				executor.Execute(new TopologyAnalysisCase(type, nodesCount));
			}
		}
	}

	public static void main(String[] args) {
		forEachCase(new TopologyAnalysis());
	}

	@Override
	public void Execute(TopologyAnalysisCase tac) {
		// Measure:
		// - Average average degree
		// - Average diameter
		// - Average clustering coefficient
	}

}
