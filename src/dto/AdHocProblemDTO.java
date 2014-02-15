package dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ad-hoc-problem")
public class AdHocProblemDTO {

	@XmlElement(name = "graph")
	private GraphDTO graph;

	@XmlElement(name = "group-member")
	private List<Integer> group;

	@XmlElement(name = "finder-name")
	private String finderName;
	
	AdHocProblemDTO() {
	}

	public AdHocProblemDTO(GraphDTO graph, List<Integer> group,
			String finderName) {
		this.graph = graph;
		this.group = group;
		this.finderName = finderName;
	}

	public GraphDTO getGraph() {
		return graph;
	}

	public List<Integer> getGroup() {
		return group;
	}

	public String getFinderName() {
		return finderName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((finderName == null) ? 0 : finderName.hashCode());
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		AdHocProblemDTO other = (AdHocProblemDTO) obj;
		if (finderName == null) {
			if (other.finderName != null)
				return false;
		} else if (!finderName.equals(other.finderName))
			return false;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		if (group == null) {
			if (other.group != null)
				return false;
		} else if (!group.equals(other.group))
			return false;
		return true;
	}
}
