package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryTopologyResultAttributeSelectorDiameterCost implements
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
