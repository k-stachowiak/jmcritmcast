package dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class GraphDTO {

	@XmlElement(name = "node")
	private List<NodeDTO> nodes;
	
	@XmlElement(name = "edge")
	private List<EdgeDTO> edges;

	GraphDTO() {
	}

	public GraphDTO(List<NodeDTO> nodes, List<EdgeDTO> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public final List<NodeDTO> getNodes() {
		return nodes;
	}

	public final List<EdgeDTO> getEdges() {
		return edges;
	}

	final void setNodes(List<NodeDTO> nodes) {
		this.nodes = nodes;
	}

	final void setEdges(List<EdgeDTO> edges) {
		this.edges = edges;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphDTO other = (GraphDTO) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Nodes:\n");
		for (NodeDTO node : nodes) {
			sb.append(node.toString());
			sb.append('\n');
		}
		sb.append("Edges:\n");
		for (EdgeDTO edge : edges) {
			sb.append(edge.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
