package apps.topanal;

public class TopologyExperiment {
	private final TopologyExperimentCase experimentCase;
	private final TopologyExperimentValues experimentValues;

	public TopologyExperiment(TopologyExperimentCase resultCase, TopologyExperimentValues resultValues) {
		this.experimentCase = resultCase;
		this.experimentValues = resultValues;
	}

	public TopologyExperimentCase getExperimentCase() {
		return experimentCase;
	}

	public TopologyExperimentValues getExperimentValues() {
		return experimentValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((experimentCase == null) ? 0 : experimentCase.hashCode());
		result = prime
				* result
				+ ((experimentValues == null) ? 0 : experimentValues.hashCode());
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
		TopologyExperiment other = (TopologyExperiment) obj;
		if (experimentCase == null) {
			if (other.experimentCase != null)
				return false;
		} else if (!experimentCase.equals(other.experimentCase))
			return false;
		if (experimentValues == null) {
			if (other.experimentValues != null)
				return false;
		} else if (!experimentValues.equals(other.experimentValues))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [experimentCase=" + experimentCase
				+ ", experimentValues=" + experimentValues + "]";
	}
}
