package apps.constranal;

public class ConstraintExperimentValues {
	public static class Range {
		private final double min;
		private final double max;

		public Range(double min, double max) {
			this.min = min;
			this.max = max;
		}

		public double getMin() {
			return min;
		}

		public double getMax() {
			return max;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(max);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(min);
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
			Range other = (Range) obj;
			if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
				return false;
			if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Range [min=" + min + ", max=" + max + "]";
		}

	}

	private final Range range0;
	private final Range range1;

	public ConstraintExperimentValues(Range range0, Range range1) {
		this.range0 = range0;
		this.range1 = range1;
	}

	public Range getRange0() {
		return range0;
	}

	public Range getRange1() {
		return range1;
	}

	public boolean isValid() {
		return range0.getMin() != -1 && range0.getMax() != -1 && range1.getMin() != -1 && range1.getMax() != -1;
	}

}
