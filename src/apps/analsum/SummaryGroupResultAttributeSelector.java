package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public interface SummaryGroupResultAttributeSelector {
	String getName();
	SummaryStatistics select(SummaryGroupResults value);
}
