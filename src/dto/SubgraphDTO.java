package dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.topology.Edge;
import model.topology.SubGraph;

@XmlRootElement(name = "subgraph")
public class SubgraphDTO {

	@XmlElement(name = "edges")
	private List<EdgeDTO> edges;

	SubgraphDTO() {
	}

	public SubgraphDTO(List<EdgeDTO> edges) {
		this.edges = edges;
	}

	public static SubgraphDTO fromSubgraph(SubGraph subgraph) {
		List<EdgeDTO> edges = new ArrayList<>();
		for (Edge edge : subgraph.getEdges()) {
			edges.add(new EdgeDTO(edge.getFrom(), edge.getTo(), edge.getMetrics()));
		}
		return new SubgraphDTO(edges);
	}

	public List<EdgeDTO> getEdges() {
		return edges;
	}

	final void setEdges(List<EdgeDTO> edges) {
		this.edges = edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
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
		SubgraphDTO other = (SubgraphDTO) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubgraphDTO [edges=" + edges + "]";
	}
}
