package impossible.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class EdgeDTO {

	@XmlElement(name = "from")
	private int nodeFrom;
	
	@XmlElement(name = "to")
	private int nodeTo;
	
	@XmlElement(name = "metric")
	private List<Double> metrics;

	EdgeDTO() {
	}

	public EdgeDTO(int nodeFrom, int nodeTo, List<Double> metrics) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.metrics = metrics;
	}

	public int getNodeFrom() {
		return nodeFrom;
	}

	public int getNodeTo() {
		return nodeTo;
	}

	public List<Double> getMetrics() {
		return metrics;
	}

	void setNodeFrom(int nodeFrom) {
		this.nodeFrom = nodeFrom;
	}

	void setNodeTo(int nodeTo) {
		this.nodeTo = nodeTo;
	}

	void setMetrics(List<Double> metrics) {
		this.metrics = metrics;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
		result = prime * result + nodeFrom;
		result = prime * result + nodeTo;
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
		EdgeDTO other = (EdgeDTO) obj;
		if (metrics == null) {
			if (other.metrics != null)
				return false;
		} else if (!metrics.equals(other.metrics))
			return false;
		if (nodeFrom != other.nodeFrom)
			return false;
		if (nodeTo != other.nodeTo)
			return false;
		return true;
	}

}
