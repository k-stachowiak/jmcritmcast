package apps.analsum.cstr;

public class SummaryConstraintResults {
	
	private final SummaryConstraintResult metric1Result = new SummaryConstraintResult();
	private final SummaryConstraintResult metric2Result = new SummaryConstraintResult();

	public void put(double min1, double max1, double min2,
			double max2) {
		metric1Result.put(min1, max1);
		metric2Result.put(min2, max2);
	}

	public SummaryConstraintResult getMetric1() {
		return metric1Result;
	}
	
	public SummaryConstraintResult getMetric2() {
		return metric2Result;
	}
}
