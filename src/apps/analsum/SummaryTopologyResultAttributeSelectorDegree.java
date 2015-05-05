package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class SummaryTopologyResultAttributeSelectorDegree implements
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
