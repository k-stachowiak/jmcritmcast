package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryGroupResultAttributeSelectorDiameterCost implements
		SummaryGroupResultAttributeSelector {

	@Override
	public String getName() {
		return "DiameterCost";
	}

	@Override
	public SummaryStatistics select(SummaryGroupResults value) {
		return value.diameterCost;
	}

}
