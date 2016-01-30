package apps.analsum.algo;

import helpers.nodegrp.NodeGroupperType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import tfind.TreeFinderType;
import apps.CommonConfig;
import apps.alganal.AlgorithmExperimentCase;
import dal.TopologyType;

public class SummaryAlgorithmResultTable {
	private final LinkedHashMap<TopologyType, LinkedHashMap<Integer, // Nodes count
	LinkedHashMap<Integer, // Group size
	LinkedHashMap<NodeGroupperType, LinkedHashMap<List<Double>, // Constraints
	LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>>>> impl;

	public SummaryAlgorithmResultTable() {
		impl = new LinkedHashMap<>();
		for (TopologyType topologyType : TopologyType.values()) {
			LinkedHashMap<Integer, // Nodes count
			LinkedHashMap<Integer, // Group size
			LinkedHashMap<NodeGroupperType, LinkedHashMap<List<Double>, // Constraints
			LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>>> nodesMap = new LinkedHashMap<>();

			for (int nodesCount : CommonConfig.nodesCounts) {
				LinkedHashMap<Integer, // Group size
				LinkedHashMap<NodeGroupperType, LinkedHashMap<List<Double>, // Constraints
				LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>> groupMap = new LinkedHashMap<>();

				for (int groupSize : CommonConfig.groupSizes) {
					LinkedHashMap<NodeGroupperType, LinkedHashMap<List<Double>, // Constraints
					LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>> groupperMap = new LinkedHashMap<>();

					for (NodeGroupperType nodeGroupperType : NodeGroupperType
							.values()) {
						LinkedHashMap<List<Double>, // Constraints
						LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>> constraintsMap = new LinkedHashMap<>();

						for (List<Double> constraints : CommonConfig.constraintSets) {
							LinkedHashMap<TreeFinderType, SummaryAlgorithmResults> treeFinderMap = new LinkedHashMap<>();

							for (TreeFinderType treeFinderType : TreeFinderType
									.values()) {
								treeFinderMap.put(treeFinderType,
										new SummaryAlgorithmResults(3));
							}

							constraintsMap.put(constraints, treeFinderMap);
						}

						groupperMap.put(nodeGroupperType, constraintsMap);
					}

					groupMap.put(groupSize, groupperMap);
				}

				nodesMap.put(nodesCount, groupMap);
			}

			impl.put(topologyType, nodesMap);
		}
	}

	public SummaryAlgorithmResults selectResults(
			AlgorithmExperimentCase experimentCase) {
		
		ArrayList<Double> constraints = new ArrayList<>();
		constraints.add(experimentCase.getConstraint1());
		constraints.add(experimentCase.getConstraint2());
		
		return impl.get(experimentCase.getTopologyType())
				.get(experimentCase.getNodesCount())
				.get(experimentCase.getGroupSize())
				.get(experimentCase.getNodeGroupperType())
				.get(constraints)
				.get(experimentCase.getTreeFinderType());
	}

	public Iterator<Entry<TreeFinderType, SummaryAlgorithmResults>> selectRow(
			TopologyType topologyType,
			int nodesCount,
			int groupSize,
			NodeGroupperType nodeGroupperType,
			double constraint1,
			double constraint2) {
		
		ArrayList<Double> constraints = new ArrayList<>();
		constraints.add(constraint1);
		constraints.add(constraint2);
		
		return impl.get(topologyType).get(nodesCount).get(groupSize)
				.get(nodeGroupperType).get(constraints).entrySet()
				.iterator();
	}
}
