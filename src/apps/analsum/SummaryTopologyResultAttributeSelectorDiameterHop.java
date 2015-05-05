package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryTopologyResultAttributeSelectorDiameterHop implements
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
