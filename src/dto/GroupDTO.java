package dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import model.topology.Node;

@XmlRootElement(name = "group")
public class GroupDTO {

	@XmlElement(name = "nodes")
	private List<NodeDTO> nodes;

	GroupDTO() {
	}

	public GroupDTO(List<NodeDTO> nodes) {
		this.nodes = nodes;
	}

	public static GroupDTO fromNodeList(List<Node> group) {
		List<NodeDTO> nodes = new ArrayList<>();
		for (Node node : group) {
			nodes.add(new NodeDTO(node.getId(), node.getX(), node.getY()));
		}
		return new GroupDTO(nodes);
	}

	public List<NodeDTO> getNodes() {
		return nodes;
	}

	final void setNodes(List<NodeDTO> nodes) {
		this.nodes = nodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		GroupDTO other = (GroupDTO) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GroupDTO [nodes=" + nodes + "]";
	}
}
