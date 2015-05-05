package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryGroupResultAttributeSelectorDegree implements
		SummaryGroupResultAttributeSelector {

	@Override
	public String getName() {
		return "Degree";
	}

	@Override
	public SummaryStatistics select(SummaryGroupResults value) {
		return value.degree;
	}

}
