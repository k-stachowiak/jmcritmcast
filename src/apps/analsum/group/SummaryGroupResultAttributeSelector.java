package apps.analsum.group;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public interface SummaryGroupResultAttributeSelector {
	String getName();

	SummaryStatistics select(SummaryGroupResults value);

	public class ClusteringCoefficient
			implements SummaryGroupResultAttributeSelector {

		@Override
		public String getName() {
			return "ClusteringCoefficient";
		}

		@Override
		public SummaryStatistics select(SummaryGroupResults value) {
			return value.clusteringCoefficient;
		}

	}

	public class Degree implements SummaryGroupResultAttributeSelector {

		@Override
		public String getName() {
			return "Degree";
		}

		@Override
		public SummaryStatistics select(SummaryGroupResults value) {
			return value.degree;
		}

	}

	public class Density implements SummaryGroupResultAttributeSelector {

		@Override
		public String getName() {
			return "Density";
		}

		@Override
		public SummaryStatistics select(SummaryGroupResults value) {
			return value.density;
		}

	}

	public class DiameterCost implements SummaryGroupResultAttributeSelector {

		@Override
		public String getName() {
			return "DiameterCost";
		}

		@Override
		public SummaryStatistics select(SummaryGroupResults value) {
			return value.diameterCost;
		}

	}

	public class DiameterHop implements SummaryGroupResultAttributeSelector {

		@Override
		public String getName() {
			return "DiameterHop";
		}

		@Override
		public SummaryStatistics select(SummaryGroupResults value) {
			return value.diameterHop;
		}

	}

}
