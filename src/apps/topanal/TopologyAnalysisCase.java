package apps.topanal;

public class TopologyAnalysisCase {
	private final TopologyType type;
	private final int nodesCount;

	public TopologyAnalysisCase(TopologyType type, int nodesCount) {
		super();
		this.type = type;
		this.nodesCount = nodesCount;
	}

	public TopologyType getType() {
		return type;
	}

	public int getNodesCount() {
		return nodesCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nodesCount;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TopologyAnalysisCase other = (TopologyAnalysisCase) obj;
		if (nodesCount != other.nodesCount)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
