package edu.put.et.stik.mm.apps;

import java.util.List;

public class MultiCostSetup {
	private final long randomSeed;

	private final double fengDelta;

	private final int graphs;

	private final List<Integer> nodeSizes;
	private final List<Integer> criteriaCounts;
	private final List<Integer> groupSizes;

	private final String topologiesDirectory;
	private final String topology;
	private final int graphsInFile;

	private final double redistributionMin;
	private final double redistributionMax;

	private final List<String> treeFinderNames;

	public MultiCostSetup(long randomSeed, double fengDelta, int graphs,
			List<Integer> nodeSizes, List<Integer> criteriaCounts,
			List<Integer> groupSizes, String topologiesDirectory,
			String topology, int graphsInFile, double redistributionMin,
			double redistributionMax, List<String> treeFinderNames) {

		this.randomSeed = randomSeed;
		this.fengDelta = fengDelta;
		this.graphs = graphs;
		this.nodeSizes = nodeSizes;
		this.criteriaCounts = criteriaCounts;
		this.groupSizes = groupSizes;
		this.topologiesDirectory = topologiesDirectory;
		this.topology = topology;
		this.graphsInFile = graphsInFile;
		this.redistributionMin = redistributionMin;
		this.redistributionMax = redistributionMax;
		this.treeFinderNames = treeFinderNames;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public double getFengDelta() {
		return fengDelta;
	}

	public int getGraphs() {
		return graphs;
	}

	public List<Integer> getNodeSizes() {
		return nodeSizes;
	}

	public List<Integer> getCriteriaCounts() {
		return criteriaCounts;
	}

	public List<Integer> getGroupSizes() {
		return groupSizes;
	}

	public String getTopologiesDirectory() {
		return topologiesDirectory;
	}

	public String getTopology() {
		return topology;
	}

	public int getGraphsInFile() {
		return graphsInFile;
	}

	public double getRedistributionMin() {
		return redistributionMin;
	}

	public double getRedistributionMax() {
		return redistributionMax;
	}

	public List<String> getTreeFinderNames() {
		return treeFinderNames;
	}
}
