package apps.groupanal;

import helpers.nodegrp.NodeGroupperType;
import dal.TopologyType;

public class GroupExperimentCase {
	private final TopologyType topologyType;
	private final int nodesCount;
	private final int groupSize;
	private final NodeGroupperType nodeGroupperType;

	public GroupExperimentCase(TopologyType topologyType, int nodesCount,
			int groupSize, NodeGroupperType nodeGroupperType) {
		this.topologyType = topologyType;
		this.nodesCount = nodesCount;
		this.groupSize = groupSize;
		this.nodeGroupperType = nodeGroupperType;
	}

	public TopologyType getTopologyType() {
		return topologyType;
	}

	public int getNodesCount() {
		return nodesCount;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public NodeGroupperType getNodeGroupperType() {
		return nodeGroupperType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupSize;
		result = prime
				* result
				+ ((nodeGroupperType == null) ? 0 : nodeGroupperType.hashCode());
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
		GroupExperimentCase other = (GroupExperimentCase) obj;
		if (groupSize != other.groupSize)
			return false;
		if (nodeGroupperType != other.nodeGroupperType)
			return false;
		if (nodesCount != other.nodesCount)
			return false;
		if (topologyType != other.topologyType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExperimentCase [topologyType=" + topologyType + ", nodesCount="
				+ nodesCount + ", groupSize=" + groupSize
				+ ", nodeGroupperType=" + nodeGroupperType + "]";
	}
}
