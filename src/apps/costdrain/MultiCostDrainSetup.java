package apps.costdrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MultiCostDrainSetup {
	private final long randomSeed;

	private final List<List<Double>> constraintCases;

	private final double baseBandwidth;
	private final double drainedBandwidth;
	private final double minBandwidth;
	private final int drainedIndex;

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

	public MultiCostDrainSetup(Properties properties) {

		// Peal out numeric values and collections.
		// ----------------------------------------
		randomSeed = Long.parseLong(properties.getProperty("randomSeed"));
		constraintCases = parseConstraintCases(properties
				.getProperty("constraintCases"));
		baseBandwidth = Double.parseDouble(properties
				.getProperty("baseBandwidth"));
		drainedBandwidth = Double.parseDouble(properties
				.getProperty("drainedBandwidth"));
		minBandwidth = Double.parseDouble(properties
				.getProperty("minBandwidth"));
		drainedIndex = Integer.parseInt(properties.getProperty("drainedIndex"));
		graphsInFile = Integer.parseInt(properties.getProperty("graphsInFile"));
		redistributionMin = Double.parseDouble(properties
				.getProperty("redistributionMin"));
		redistributionMax = Double.parseDouble(properties
				.getProperty("redistributionMax"));
		graphs = Integer.parseInt(properties.getProperty("graphs"));

		String nssStr = properties.getProperty("nodeSizes");
		String[] nss = nssStr.split(",");
		nodeSizes = new ArrayList<>();
		for (String ns : nss) {
			nodeSizes.add(Integer.parseInt(ns));
		}

		String ccsStr = properties.getProperty("criteriaCounts");
		String[] ccs = ccsStr.split(",");
		criteriaCounts = new ArrayList<>();
		for (String cc : ccs) {
			criteriaCounts.add(Integer.parseInt(cc));
		}

		String gssStr = properties.getProperty("groupSizes");
		String[] gss = gssStr.split(",");
		groupSizes = new ArrayList<>();
		for (String gs : gss) {
			groupSizes.add(Integer.parseInt(gs));
		}

		// Read the string properties.
		// ---------------------------
		topologiesDirectory = properties.getProperty("topologiesDirectory");
		topology = properties.getProperty("topology");
		String ansStr = properties.getProperty("algNames");

		if (topologiesDirectory == null) {
			throw new RuntimeException("Error while parsing topologies directory property.");
		}

		if (topology == null) {
			throw new RuntimeException("Exception: Error while parsing topology property.");
		}

		if (ansStr == null) {
			throw new RuntimeException("Exception: Error while parsing algorithm names property.");
		}

		treeFinderNames = new ArrayList<>(Arrays.asList(ansStr.split(",")));
	}

	private static List<List<Double>> parseConstraintCases(String property) {

		List<List<Double>> result = new ArrayList<>();

		for (String constraintSetStr : property.split(";")) {
			List<Double> constraintSet = new ArrayList<>();
			for (String constraint : constraintSetStr.split("\\s+")) {
				constraintSet.add(Double.parseDouble(constraint));
			}
			result.add(constraintSet);
		}

		return result;
	}

	public long getRandomSeed() {
		return randomSeed;
	}

	public List<List<Double>> GetConstraintCases() {
		return constraintCases;
	}

	public double getBaseBandwidth() {
		return baseBandwidth;
	}

	public double getDrainedBandwidth() {
		return drainedBandwidth;
	}

	public double getMinBandwidth() {
		return minBandwidth;
	}

	public int getDrainedIndex() {
		return drainedIndex;
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
