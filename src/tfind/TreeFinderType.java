package tfind;

public enum TreeFinderType {
	HMCMC, AggrMLARAC, RDP;

	@Override
	public String toString() {
		return name();
	}
}
