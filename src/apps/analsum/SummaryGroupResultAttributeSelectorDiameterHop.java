package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryGroupResultAttributeSelectorDiameterHop implements
		SummaryGroupResultAttributeSelector {

	@Override
	public String getName() {
		return "DiameterHop";
	}

	@Override
	public SummaryStatistics select(SummaryGroupResults value) {
		return value.diameterHop;
	}

}
