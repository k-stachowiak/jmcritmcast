package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public interface SummaryTopologyResultAttributeSelector {
	
	String getName();
	SummaryStatistics select(SummaryTopologyResults value);

	public class ClusteringCoefficient
			implements SummaryTopologyResultAttributeSelector {

		@Override
		public String getName() {
			return "ClusteringCoefficient";
		}

		@Override
		public SummaryStatistics select(SummaryTopologyResults value) {
			return value.clusteringCoefficient;
		}

	}

	public class Degree implements
			SummaryTopologyResultAttributeSelector {

		@Override
		public String getName() {
			return "Degree";
		}

		@Override
		public SummaryStatistics select(SummaryTopologyResults value) {
			return value.degree;
		}

	}

	public class DiameterCost implements
			SummaryTopologyResultAttributeSelector {

		@Override
		public String getName() {
			return "DiameterCost";
		}

		@Override
		public SummaryStatistics select(SummaryTopologyResults value) {
			return value.diameterCost;
		}

	}

	public class DiameterHop implements
			SummaryTopologyResultAttributeSelector {

		@Override
		public String getName() {
			return "DiameterHop";
		}

		@Override
		public SummaryStatistics select(SummaryTopologyResults value) {
			return value.diameterHop;
		}

	}

}
