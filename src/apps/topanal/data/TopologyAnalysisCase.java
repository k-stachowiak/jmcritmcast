package apps.topanal.data;

import java.io.PrintStream;

public class TopologyAnalysisCase {
	private final TopologyType type;
	private final int nodesCount;
	private final int graphsCount;

	public TopologyAnalysisCase(TopologyType type, int nodesCount, int graphsCount) {
		this.type = type;
		this.nodesCount = nodesCount;
		this.graphsCount = graphsCount;
	}

	public TopologyType getType() {
		return type;
	}

	public int getNodesCount() {
		return nodesCount;
	}
	
	public int getGraphsCount() {
		return graphsCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + graphsCount;
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
		if (graphsCount != other.graphsCount)
			return false;
		if (nodesCount != other.nodesCount)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public static void printHeader(PrintStream out) {
		out.print("type\tnodes\tgraphs\t");
	}

	public void print(PrintStream out) {
		out.printf("%s\t%d\t%d\t", type.name(), nodesCount, graphsCount);
	}
}
