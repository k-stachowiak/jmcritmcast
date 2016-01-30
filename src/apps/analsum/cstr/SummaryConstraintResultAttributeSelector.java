package apps.analsum.cstr;

public interface SummaryConstraintResultAttributeSelector {

	public class Index implements SummaryConstraintResultAttributeSelector {
		private final int index;

		public Index(int index) {
			this.index = index;
		}

		@Override
		public SummaryConstraintResult select(SummaryConstraintResults value) {
			if (index == 1) {
				return value.getMetric1();
			} else if (index == 2) {
				return value.getMetric2();
			} else {
				throw new RuntimeException("Incorrect metric index");
			}
		}

		@Override
		public String getName() {
			return "Index" + index;
		}
	}

	SummaryConstraintResult select(SummaryConstraintResults value);
	
	String getName();

}
