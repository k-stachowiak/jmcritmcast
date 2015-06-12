package apps.alganal;

public class AlgorithmExperiment {
	private final AlgorithmExperimentCase experimentCase;
	private final AlgorithmExperimentValues experimentValues;

	public AlgorithmExperiment(AlgorithmExperimentCase experimentCase,
			AlgorithmExperimentValues experimentValues) {
		super();
		this.experimentCase = experimentCase;
		this.experimentValues = experimentValues;
	}

	public AlgorithmExperimentCase getExperimentCase() {
		return experimentCase;
	}

	public AlgorithmExperimentValues getExperimentValues() {
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
		AlgorithmExperiment other = (AlgorithmExperiment) obj;
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
		return "AlgorithmExperiment [experimentCase=" + experimentCase
				+ ", experimentValues=" + experimentValues + "]";
	}
}
