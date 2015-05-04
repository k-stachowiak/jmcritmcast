package apps.groupanal;

public class GroupExperimentValues {

	private final int graphIndex;
	private final double degree;
	private final double diameterHop;
	private final double diameterCost;
	private final double clusteringCoefficient;
	private final double density;

	public GroupExperimentValues(int graphIndex, double degree,
			double diameterHop, double diameterCost,
			double clusteringCoefficient, double density) {
		this.graphIndex = graphIndex;
		this.degree = degree;
		this.diameterHop = diameterHop;
		this.diameterCost = diameterCost;
		this.clusteringCoefficient = clusteringCoefficient;
		this.density = density;
	}

	public int getGraphIndex() {
		return graphIndex;
	}

	public double getDegree() {
		return degree;
	}

	public double getDiameterHop() {
		return diameterHop;
	}

	public double getDiameterCost() {
		return diameterCost;
	}

	public double getClusteringCoefficient() {
		return clusteringCoefficient;
	}

	public double getDensity() {
		return density;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(clusteringCoefficient);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(degree);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(density);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(diameterCost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(diameterHop);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + graphIndex;
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
		GroupExperimentValues other = (GroupExperimentValues) obj;
		if (Double.doubleToLongBits(clusteringCoefficient) != Double
				.doubleToLongBits(other.clusteringCoefficient))
			return false;
		if (Double.doubleToLongBits(degree) != Double
				.doubleToLongBits(other.degree))
			return false;
		if (Double.doubleToLongBits(density) != Double
				.doubleToLongBits(other.density))
			return false;
		if (Double.doubleToLongBits(diameterCost) != Double
				.doubleToLongBits(other.diameterCost))
			return false;
		if (Double.doubleToLongBits(diameterHop) != Double
				.doubleToLongBits(other.diameterHop))
			return false;
		if (graphIndex != other.graphIndex)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExperimentValues [graphIndex=" + graphIndex + ", degree="
				+ degree + ", diameterHop=" + diameterHop + ", diameterCost="
				+ diameterCost + ", clusteringCoefficient="
				+ clusteringCoefficient + ", density=" + density + "]";
	}
}
