package apps.topanal.data;

import java.io.PrintStream;

public class TopologyAnalysisMacroResult {
	private final double avgDegree;
	private final double avgDiameter;
	private final double avgClusteringCoefficient;

	public TopologyAnalysisMacroResult(double avgDegree, double avgDiameter,
			double avgClusteringCoefficient) {
		this.avgDegree = avgDegree;
		this.avgDiameter = avgDiameter;
		this.avgClusteringCoefficient = avgClusteringCoefficient;
	}

	public double getAvgDegree() {
		return avgDegree;
	}

	public double getAvgDiameter() {
		return avgDiameter;
	}

	public double getAvgClusteringCoefficient() {
		return avgClusteringCoefficient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(avgClusteringCoefficient);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(avgDegree);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(avgDiameter);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		TopologyAnalysisMacroResult other = (TopologyAnalysisMacroResult) obj;
		if (Double.doubleToLongBits(avgClusteringCoefficient) != Double
				.doubleToLongBits(other.avgClusteringCoefficient))
			return false;
		if (Double.doubleToLongBits(avgDegree) != Double
				.doubleToLongBits(other.avgDegree))
			return false;
		if (Double.doubleToLongBits(avgDiameter) != Double
				.doubleToLongBits(other.avgDiameter))
			return false;
		return true;
	}

	public static void printHeader(PrintStream out) {
		out.print("avgDeg\tavgDiam\tavgCC\t");
		//out.print("avgDeg\tavgCC\t");
	}

	public void print(PrintStream out) {
		out.printf("%f\t%f\t%f\t", avgDegree, avgDiameter, avgClusteringCoefficient);
		//out.printf("%f\t%f\t", avgDegree, avgClusteringCoefficient);
	}

}
