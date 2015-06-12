package apps.alganal;

import java.util.List;

public class AlgorithmExperimentValues {

	private final List<Double> firstCosts;
	private final int successCount;

	public AlgorithmExperimentValues(List<Double> firstCosts, int successCount) {
		this.firstCosts = firstCosts;
		this.successCount = successCount;
	}

	public List<Double> getFirstCosts() {
		return firstCosts;
	}

	public int getSuccessCount() {
		return successCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstCosts == null) ? 0 : firstCosts.hashCode());
		result = prime * result + successCount;
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
		AlgorithmExperimentValues other = (AlgorithmExperimentValues) obj;
		if (firstCosts == null) {
			if (other.firstCosts != null)
				return false;
		} else if (!firstCosts.equals(other.firstCosts))
			return false;
		if (successCount != other.successCount)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlgorithmExperimentValues [firstCosts=" + firstCosts
				+ ", successCount=" + successCount + "]";
	}

	public boolean isValid() {
		return firstCosts.size() != 0 && successCount != -1;
	}
}
