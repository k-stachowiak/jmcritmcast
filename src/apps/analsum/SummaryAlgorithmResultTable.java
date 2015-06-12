package apps.analsum;

import helpers.nodegrp.NodeGroupperType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import tfind.TreeFinderType;
import apps.CommonConfig;
import apps.alganal.AlgorithmExperimentCase;
import dal.TopologyType;

public class SummaryAlgorithmResultTable {
	private final LinkedHashMap<TopologyType, LinkedHashMap<Integer, // Nodes count
	LinkedHashMap<Integer, // Group size
	LinkedHashMap<NodeGroupperType, LinkedHashMap<Double, // Constraints base
	LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>>>> impl;

	public SummaryAlgorithmResultTable() {
		impl = new LinkedHashMap<>();
		for (TopologyType topologyType : TopologyType.values()) {
			LinkedHashMap<Integer, // Nodes count
			LinkedHashMap<Integer, // Group size
			LinkedHashMap<NodeGroupperType, LinkedHashMap<Double, // Constraints base
			LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>>> nodesMap = new LinkedHashMap<>();

			for (int nodesCount : CommonConfig.nodesCounts) {
				LinkedHashMap<Integer, // Group size
				LinkedHashMap<NodeGroupperType, LinkedHashMap<Double, // Constraints base
				LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>>> groupMap = new LinkedHashMap<>();

				for (int groupSize : CommonConfig.groupSizes) {
					LinkedHashMap<NodeGroupperType, LinkedHashMap<Double, // Constraints base
					LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>>> groupperMap = new LinkedHashMap<>();

					for (NodeGroupperType nodeGroupperType : NodeGroupperType
							.values()) {
						LinkedHashMap<Double, // Constraints base
						LinkedHashMap<TreeFinderType, SummaryAlgorithmResults>> constraintsMap = new LinkedHashMap<>();

						for (Double constraintsBase : CommonConfig.constraintBases) {
							LinkedHashMap<TreeFinderType, SummaryAlgorithmResults> treeFinderMap = new LinkedHashMap<>();

							for (TreeFinderType treeFinderType : TreeFinderType
									.values()) {
								treeFinderMap.put(treeFinderType,
										new SummaryAlgorithmResults(2));
							}

							constraintsMap.put(constraintsBase, treeFinderMap);
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
		return impl.get(experimentCase.getTopologyType())
				.get(experimentCase.getNodesCount())
				.get(experimentCase.getGroupSize())
				.get(experimentCase.getNodeGroupperType())
				.get(experimentCase.getConstraintBase())
				.get(experimentCase.getTreeFinderType());
	}

	public Iterator<Entry<TreeFinderType, SummaryAlgorithmResults>> selectRow(
			TopologyType topologyType, int nodesCount, int groupSize,
			NodeGroupperType nodeGroupperType, double constraintsBase) {
		return impl.get(topologyType).get(nodesCount).get(groupSize)
				.get(nodeGroupperType).get(constraintsBase).entrySet()
				.iterator();
	}
}
