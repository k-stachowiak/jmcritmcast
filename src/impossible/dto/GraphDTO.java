package impossible.dto;

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

	public List<NodeDTO> getNodes() {
		return nodes;
	}

	public List<EdgeDTO> getEdges() {
		return edges;
	}

	void setNodes(List<NodeDTO> nodes) {
		this.nodes = nodes;
	}

	void setEdges(List<EdgeDTO> edges) {
		this.edges = edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
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

}
