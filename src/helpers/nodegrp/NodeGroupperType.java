package helpers.nodegrp;

public enum NodeGroupperType {
	Centroid02, Centroid06, Degree, Random;

	@Override
	public String toString() {
		return name();
	}

	public String argsString() {
		return "";
	}
}
