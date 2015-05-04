package apps.topanal;

import dal.TopologyType;

public class TopologyExperimentCase {
	private final TopologyType topologyType;
	private final int nodesCount;

	public TopologyExperimentCase(TopologyType type, int nodesCount) {
		this.topologyType = type;
		this.nodesCount = nodesCount;
	}

	public TopologyType getTopologyType() {
		return topologyType;
	}

	public int getNodesCount() {
		return nodesCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nodesCount;
		result = prime * result + ((topologyType == null) ? 0 : topologyType.hashCode());
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
		if (nodesCount != other.nodesCount)
			return false;
		if (topologyType != other.topologyType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResultCase [type=" + topologyType + ", nodesCount=" + nodesCount + "]";
	}
}
