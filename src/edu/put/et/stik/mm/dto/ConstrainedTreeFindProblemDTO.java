package edu.put.et.stik.mm.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "problem")
public class ConstrainedTreeFindProblemDTO {

	@XmlElement(name = "graph")
	private GraphDTO graph;

	@XmlElement(name = "member")
	private List<Integer> group;

	@XmlElement(name = "constraint")
	private List<Double> constraints;

	@XmlElement(name = "finder-name")
	private String finderName;

	public ConstrainedTreeFindProblemDTO(GraphDTO graph, List<Integer> group,
			List<Double> constraints, String finderName) {
		this.graph = graph;
		this.group = group;
		this.constraints = constraints;
		this.finderName = finderName;
	}

	ConstrainedTreeFindProblemDTO() {
	}

	public final GraphDTO getGraph() {
		return graph;
	}

	public final List<Integer> getGroup() {
		return group;
	}

	public final List<Double> getConstraints() {
		return constraints;
	}

	public final String getFinderName() {
		return finderName;
	}

	final void setGraph(GraphDTO graph) {
		this.graph = graph;
	}

	final void setGroup(List<Integer> group) {
		this.group = group;
	}

	final void setConstraints(List<Double> constraints) {
		this.constraints = constraints;
	}

	final void setFinderName(String finderName) {
		this.finderName = finderName;
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result
				+ ((finderName == null) ? 0 : finderName.hashCode());
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		result = prime * result + ((group == null) ? 0 : group.hashCode());
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
		ConstrainedTreeFindProblemDTO other = (ConstrainedTreeFindProblemDTO) obj;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
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
