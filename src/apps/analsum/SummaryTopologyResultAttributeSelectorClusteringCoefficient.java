package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryTopologyResultAttributeSelectorClusteringCoefficient implements
		SummaryTopologyResultAttributeSelector {

	@Override
	public String getName() {
		return "ClusteringCoefficient";
	}
	
	@Override
	public SummaryStatistics select(SummaryTopologyResults value) {
		return value.clusteringCoefficient;
	}

}
