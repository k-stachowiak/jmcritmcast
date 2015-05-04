package apps.groupanal;

public class GroupExperiment {
	private final GroupExperimentCase experimentCase;
	private final GroupExperimentValues experimentValues;

	public GroupExperiment(GroupExperimentCase experimentCase,
			GroupExperimentValues experimentValues) {
		this.experimentCase = experimentCase;
		this.experimentValues = experimentValues;
	}

	public GroupExperimentCase getExperimentCase() {
		return experimentCase;
	}

	public GroupExperimentValues getExperimentValues() {
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
		GroupExperiment other = (GroupExperiment) obj;
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
		return "Experiment [experimentCase=" + experimentCase
				+ ", experimentValues=" + experimentValues + "]";
	}
}
