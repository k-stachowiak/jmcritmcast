package helpers.nodegrp;

public enum NodeGroupperType {
	Centroid, Degree, Random;

	@Override
	public String toString() {
		return name();
	}
}
