package apps.topanal;

public class TopologyExperimentValues {
	private Double degree;
	private Double diameterHop;
	private Double diameterCost;
	private Double clusteringCoefficient;

	public TopologyExperimentValues(Double degree, Double diameterHop,
			Double diameterCost, Double clusteringCoefficient) {
		this.degree = degree;
		this.diameterHop = diameterHop;
		this.diameterCost = diameterCost;
		this.clusteringCoefficient = clusteringCoefficient;
	}

	public Double getDegree() {
		return degree;
	}

	public void setDegree(Double degree) {
		this.degree = degree;
	}

	public Double getDiameterHop() {
		return diameterHop;
	}

	public void setDiameterHop(Double diameterHop) {
		this.diameterHop = diameterHop;
	}

	public Double getDiameterCost() {
		return diameterCost;
	}

	public void setDiameterCost(Double diameterCost) {
		this.diameterCost = diameterCost;
	}

	public Double getClusteringCoefficient() {
		return clusteringCoefficient;
	}

	public void setClusteringCoefficient(Double clusteringCoefficient) {
		this.clusteringCoefficient = clusteringCoefficient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((clusteringCoefficient == null) ? 0 : clusteringCoefficient
						.hashCode());
		result = prime * result + ((degree == null) ? 0 : degree.hashCode());
		result = prime * result
				+ ((diameterCost == null) ? 0 : diameterCost.hashCode());
		result = prime * result
				+ ((diameterHop == null) ? 0 : diameterHop.hashCode());
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
		TopologyExperimentValues other = (TopologyExperimentValues) obj;
		if (clusteringCoefficient == null) {
			if (other.clusteringCoefficient != null)
				return false;
		} else if (!clusteringCoefficient.equals(other.clusteringCoefficient))
			return false;
		if (degree == null) {
			if (other.degree != null)
				return false;
		} else if (!degree.equals(other.degree))
			return false;
		if (diameterCost == null) {
			if (other.diameterCost != null)
				return false;
		} else if (!diameterCost.equals(other.diameterCost))
			return false;
		if (diameterHop == null) {
			if (other.diameterHop != null)
				return false;
		} else if (!diameterHop.equals(other.diameterHop))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResultValues [degree=" + degree + ", diameterHop="
				+ diameterHop + ", diameterCost=" + diameterCost
				+ ", clusteringCoefficient=" + clusteringCoefficient + "]";
	}

	public boolean isValid() {
		return degree != null && diameterHop != null && diameterCost != null
				&& clusteringCoefficient != null;
	}
}
