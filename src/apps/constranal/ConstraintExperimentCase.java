package apps.constranal;

import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;

public class ConstraintExperimentCase {

	private final TopologyType topologyType;
	private final Integer nodesCount;
	private final Integer groupSize;
	private final NodeGroupperType groupperType;
	private final int graphIndex;

	public ConstraintExperimentCase(TopologyType topologyType, Integer nodesCount, Integer groupSize,
			NodeGroupperType groupperType, int graphIndex) {
		this.topologyType = topologyType;
		this.nodesCount = nodesCount;
		this.groupSize = groupSize;
		this.groupperType = groupperType;
		this.graphIndex = graphIndex;
	}

	public TopologyType getTopologyType() {
		return topologyType;
	}

	public Integer getNodesCount() {
		return nodesCount;
	}

	public Integer getGroupSize() {
		return groupSize;
	}

	public NodeGroupperType getNodeGroupperType() {
		return groupperType;
	}

	public int getGraphIndex() {
		return graphIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupperType == null) ? 0 : groupperType.hashCode());
		result = prime * result + graphIndex;
		result = prime * result + ((groupSize == null) ? 0 : groupSize.hashCode());
		result = prime * result + ((nodesCount == null) ? 0 : nodesCount.hashCode());
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
		ConstraintExperimentCase other = (ConstraintExperimentCase) obj;
		if (groupperType != other.groupperType)
			return false;
		if (graphIndex != other.graphIndex)
			return false;
		if (groupSize == null) {
			if (other.groupSize != null)
				return false;
		} else if (!groupSize.equals(other.groupSize))
			return false;
		if (nodesCount == null) {
			if (other.nodesCount != null)
				return false;
		} else if (!nodesCount.equals(other.nodesCount))
			return false;
		if (topologyType != other.topologyType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConstraintExperimentCase [topologyType=" + topologyType + ", nodesCount=" + nodesCount + ", groupSize="
				+ groupSize + ", groupperType=" + groupperType + ", graphIndex=" + graphIndex + "]";
	}

}
