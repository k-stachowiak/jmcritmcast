package apps.analsum;

import helpers.nodegrp.NodeGroupperType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import dal.TopologyType;
import apps.CommonConfig;
import apps.groupanal.GroupExperimentCase;

public class SummaryGroupResultTable {

	private final LinkedHashMap<TopologyType, LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>>>> impl;

	public SummaryGroupResultTable() {
		impl = new LinkedHashMap<>();
		for (TopologyType topologyType : TopologyType.values()) {
			LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>>> nodesMap = new LinkedHashMap<>();

			for (int nodesCount : CommonConfig.nodesCounts) {
				LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>> groupMap = new LinkedHashMap<>();

				for (int groupSize : CommonConfig.groupSizes) {
					LinkedHashMap<NodeGroupperType, SummaryGroupResults> groupperMap = new LinkedHashMap<>();

					for (NodeGroupperType nodeGroupperType : NodeGroupperType
							.values()) {
						groupperMap.put(nodeGroupperType,
								new SummaryGroupResults());
					}

					groupMap.put(groupSize, groupperMap);
				}

				nodesMap.put(nodesCount, groupMap);
			}

			impl.put(topologyType, nodesMap);
		}
	}

	public SummaryGroupResults selectResults(GroupExperimentCase experimentCase) {
		return impl.get(experimentCase.getTopologyType())
				.get(experimentCase.getNodesCount())
				.get(experimentCase.getGroupSize())
				.get(experimentCase.getNodeGroupperType());
	}

	public Iterator<Entry<NodeGroupperType, SummaryGroupResults>> selectRow(
			TopologyType topologyType, int nodesCount, int groupSize) {
		return impl.get(topologyType).get(nodesCount).get(groupSize).entrySet()
				.iterator();
	}
}
