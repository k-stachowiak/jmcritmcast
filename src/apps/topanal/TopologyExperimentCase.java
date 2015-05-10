package apps.topanal;

import dal.TopologyType;

public class TopologyExperimentCase {
	private final TopologyType topologyType;
	private final int nodesCount;
	private final int graphIndex;

	public TopologyExperimentCase(TopologyType topologyType, int nodesCount,
			int graphIndex) {
		this.topologyType = topologyType;
		this.nodesCount = nodesCount;
		this.graphIndex = graphIndex;
	}

	public TopologyType getTopologyType() {
		return topologyType;
	}

	public int getNodesCount() {
		return nodesCount;
	}

	public int getGraphIndex() {
		return graphIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + graphIndex;
		result = prime * result + nodesCount;
		result = prime * result
				+ ((topologyType == null) ? 0 : topologyType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopologyExperimentCase other = (TopologyExperimentCase) obj;
		if (graphIndex != other.graphIndex)
			return false;
		if (nodesCount != other.nodesCount)
			return false;
		if (topologyType != other.topologyType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TopologyExperimentCase [topologyType=" + topologyType
				+ ", nodesCount=" + nodesCount + ", graphIndex=" + graphIndex
				+ "]";
	}

}
