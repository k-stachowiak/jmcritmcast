package apps.alganal;

import tfind.TreeFinderType;
import helpers.nodegrp.NodeGroupperType;
import dal.TopologyType;

public class AlgorithmExperimentCase {
	private final TopologyType topologyType;
	private final int nodesCount;
	private final int groupSize;
	private final NodeGroupperType nodeGroupperType;
	private final int graphIndex;
	private final double constraint1;
	private final double constraint2;
	private final TreeFinderType treeFinderType;

	public AlgorithmExperimentCase(TopologyType topologyType, int nodesCount,
			int groupSize, NodeGroupperType nodeGroupperType, int graphIndex,
			double constraint1, double constraint2, TreeFinderType treeFinderType) {
		this.topologyType = topologyType;
		this.nodesCount = nodesCount;
		this.groupSize = groupSize;
		this.nodeGroupperType = nodeGroupperType;
		this.graphIndex = graphIndex;
		this.constraint1 = constraint1;
		this.constraint2 = constraint2;
		this.treeFinderType = treeFinderType;
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

	public int getGraphIndex() {
		return graphIndex;
	}

	public double getConstraint1() {
		return constraint1;
	}

	public double getConstraint2() {
		return constraint2;
	}

	public TreeFinderType getTreeFinderType() {
		return treeFinderType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(constraint1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(constraint2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + graphIndex;
		result = prime * result + groupSize;
		result = prime * result + ((nodeGroupperType == null) ? 0 : nodeGroupperType.hashCode());
		result = prime * result + nodesCount;
		result = prime * result + ((topologyType == null) ? 0 : topologyType.hashCode());
		result = prime * result + ((treeFinderType == null) ? 0 : treeFinderType.hashCode());
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
		AlgorithmExperimentCase other = (AlgorithmExperimentCase) obj;
		if (Double.doubleToLongBits(constraint1) != Double.doubleToLongBits(other.constraint1))
			return false;
		if (Double.doubleToLongBits(constraint2) != Double.doubleToLongBits(other.constraint2))
			return false;
		if (graphIndex != other.graphIndex)
			return false;
		if (groupSize != other.groupSize)
			return false;
		if (nodeGroupperType != other.nodeGroupperType)
			return false;
		if (nodesCount != other.nodesCount)
			return false;
		if (topologyType != other.topologyType)
			return false;
		if (treeFinderType != other.treeFinderType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlgorithmExperimentCase [topologyType=" + topologyType + ", nodesCount=" + nodesCount + ", groupSize="
				+ groupSize + ", nodeGroupperType=" + nodeGroupperType + ", graphIndex=" + graphIndex + ", constraint1="
				+ constraint1 + ", constraint2=" + constraint2 + ", treeFinderType=" + treeFinderType + "]";
	}

}
