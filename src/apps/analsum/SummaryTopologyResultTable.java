package apps.analsum;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import apps.CommonConfig;
import apps.topanal.TopologyExperimentCase;
import dal.TopologyType;

public class SummaryTopologyResultTable {

	private final LinkedHashMap<Integer, LinkedHashMap<TopologyType, SummaryTopologyResults>> impl;

	public SummaryTopologyResultTable() {
		impl = new LinkedHashMap<>();
		for (int nodesCount : CommonConfig.nodesCounts) {
			LinkedHashMap<TopologyType, SummaryTopologyResults> topMap = new LinkedHashMap<>();
			for (TopologyType topologyType : TopologyType.values()) {
				topMap.put(topologyType, new SummaryTopologyResults());
			}
			impl.put(nodesCount, topMap);
		}
	}

	public SummaryTopologyResults selectResults(
			TopologyExperimentCase experimentCase) {
		return impl.get(experimentCase.getNodesCount()).get(
				experimentCase.getTopologyType());
	}

	public Iterator<Entry<TopologyType, SummaryTopologyResults>> selectRow(
			int nodesCount) {
		return impl.get(nodesCount).entrySet().iterator();
	}
}
