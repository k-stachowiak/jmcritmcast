package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryGroupResultAttributeSelectorClusteringCoefficient implements
		SummaryGroupResultAttributeSelector {

	@Override
	public String getName() {
		return "ClusteringCoefficient";
	}
	
	@Override
	public SummaryStatistics select(SummaryGroupResults value) {
		return value.clusteringCoefficient;
	}

}
