package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryGroupResultAttributeSelectorDensity implements
		SummaryGroupResultAttributeSelector {

	@Override
	public String getName() {
		return "Density";
	}

	@Override
	public SummaryStatistics select(SummaryGroupResults value) {
		return value.density;
	}

}
