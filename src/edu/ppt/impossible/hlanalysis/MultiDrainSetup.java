package edu.ppt.impossible.hlanalysis;

import java.util.List;

public class MultiDrainSetup {
	private final long randomSeed;

	private final double fengDelta;

	private final double baseBandwidth;
	private final double drainedBandwidth;

	private final int graphs;

	private final List<Integer> nodeSizes;
	private final List<Integer> criteriaCounts;
	private final List<Integer> groupSizes;

	private final String topologiesDirectory;
	private final String topology;
	private final int graphsInFile;

	private final double redistributionMin;
	private final double redistributionMax;

	public MultiDrainSetup(long randomSeed, double fengDelta,
			double baseBandwidth, double drainedBandwidth, int graphs,
			List<Integer> nodeSizes, List<Integer> criteriaCounts,
			List<Integer> groupSizes, String topologiesDirectory,
			String topology, int graphsInFile, double redistributionMin,
			double redistributionMax) {

		this.randomSeed = randomSeed;
		this.fengDelta = fengDelta;
		this.baseBandwidth = baseBandwidth;
		this.drainedBandwidth = drainedBandwidth;
		this.graphs = graphs;
		this.nodeSizes = nodeSizes;
		this.criteriaCounts = criteriaCounts;
		this.groupSizes = groupSizes;
		this.topologiesDirectory = topologiesDirectory;
		this.topology = topology;
		this.graphsInFile = graphsInFile;
		this.redistributionMin = redistributionMin;
		this.redistributionMax = redistributionMax;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public double getFengDelta() {
		return fengDelta;
	}

	public double getBaseBandwidth() {
		return baseBandwidth;
	}

	public double getDrainedBandwidth() {
		return drainedBandwidth;
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
}
