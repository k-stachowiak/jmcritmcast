package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public interface SummaryTopologyResultAttributeSelector {
	String getName();
	SummaryStatistics select(SummaryTopologyResults value);
}
