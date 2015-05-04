package dal;

public enum TopologyType {
	ASWaxman, ASBarabasi, Inet;

	@Override
	public String toString() {
		return name();
	}

}
