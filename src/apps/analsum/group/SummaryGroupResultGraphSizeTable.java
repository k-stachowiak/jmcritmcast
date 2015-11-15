package apps.analsum.group;

import helpers.nodegrp.NodeGroupperType;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import dal.TopologyType;
import apps.CommonConfig;
import apps.groupanal.GroupExperimentCase;

public class SummaryGroupResultGraphSizeTable {

	private final LinkedHashMap<TopologyType, LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>>>> impl;

	public SummaryGroupResultGraphSizeTable() {
		impl = new LinkedHashMap<>();
		for (TopologyType topologyType : TopologyType.values()) {
			LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>>> groupMap = new LinkedHashMap<>();

			for (int groupSize : CommonConfig.groupSizes) {
				LinkedHashMap<Integer, LinkedHashMap<NodeGroupperType, SummaryGroupResults>> nodesMap = new LinkedHashMap<>();

				for (int nodesCount : CommonConfig.nodesCounts) {
					LinkedHashMap<NodeGroupperType, SummaryGroupResults> groupperMap = new LinkedHashMap<>();

					for (NodeGroupperType nodeGroupperType : NodeGroupperType
							.values()) {
						groupperMap.put(nodeGroupperType,
								new SummaryGroupResults());
					}

					nodesMap.put(nodesCount, groupperMap);
				}

				groupMap.put(groupSize, nodesMap);
			}

			impl.put(topologyType, groupMap);
		}
	}

	public SummaryGroupResults selectResults(GroupExperimentCase experimentCase) {
		return impl.get(experimentCase.getTopologyType())
				.get(experimentCase.getGroupSize())
				.get(experimentCase.getNodesCount())
				.get(experimentCase.getNodeGroupperType());
	}

	public Iterator<Entry<NodeGroupperType, SummaryGroupResults>> selectRow(
			TopologyType topologyType, int groupSize, int nodesCount) {
		return impl.get(topologyType).get(groupSize).get(nodesCount).entrySet()
				.iterator();
	}
}
