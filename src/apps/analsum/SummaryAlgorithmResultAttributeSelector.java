package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public interface SummaryAlgorithmResultAttributeSelector {

	String getName();

	SummaryStatistics select(SummaryAlgorithmResults value);

	public class FirstCost0 implements SummaryAlgorithmResultAttributeSelector {

		@Override
		public String getName() {
			return "Metric0";
		}

		@Override
		public SummaryStatistics select(SummaryAlgorithmResults value) {
			if (value.firstCosts.size() > 0) {
				return value.firstCosts.get(0);
			} else {
				return new SummaryStatistics();
			}
		}
	}

	public class FirstCost1 implements SummaryAlgorithmResultAttributeSelector {

		@Override
		public String getName() {
			return "Metric1";
		}

		@Override
		public SummaryStatistics select(SummaryAlgorithmResults value) {
			if (value.firstCosts.size() > 1) {
				return value.firstCosts.get(1);
			} else {
				return new SummaryStatistics();
			}
		}
	}

	public class FirstCost2 implements SummaryAlgorithmResultAttributeSelector {

		@Override
		public String getName() {
			return "Metric2";
		}

		@Override
		public SummaryStatistics select(SummaryAlgorithmResults value) {
			if (value.firstCosts.size() > 2) {
				return value.firstCosts.get(2);
			} else {
				return new SummaryStatistics();
			}
		}
	}

	public class FirstCost3 implements SummaryAlgorithmResultAttributeSelector {

		@Override
		public String getName() {
			return "Metric3";
		}

		@Override
		public SummaryStatistics select(SummaryAlgorithmResults value) {
			if (value.firstCosts.size() > 3) {
				return value.firstCosts.get(3);
			} else {
				return new SummaryStatistics();
			}
		}
	}

	public class SuccessCount implements
			SummaryAlgorithmResultAttributeSelector {

		@Override
		public String getName() {
			return "SuccessCount";
		}

		@Override
		public SummaryStatistics select(SummaryAlgorithmResults value) {
			return value.successCount;
		}
	}
}
