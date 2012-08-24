package impossible.stat;

public class Interval {
	
	private final double lowerBound;
	private final double upperBound;
	
	public Interval(double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}
}
