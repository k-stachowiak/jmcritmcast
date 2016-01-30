package apps.analsum.algo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.alganal.AlgorithmExperimentValues;

public class SummaryAlgorithmResults {
	public final List<SummaryStatistics> firstCosts;
	public final SummaryStatistics successCount;

	SummaryAlgorithmResults(int metricsCount) {
		firstCosts = new ArrayList<>();
		for (int i = 0; i < metricsCount; ++i) {
			firstCosts.add(new SummaryStatistics());
		}
		successCount = new SummaryStatistics();
	}

	public void insert(AlgorithmExperimentValues experimentValues) {
		successCount.addValue(experimentValues.getSuccessCount());
		if (experimentValues.getSuccessCount() > 0) {
			for (int i = 0; i < firstCosts.size(); ++i) {
				firstCosts.get(i).addValue(experimentValues.getFirstCosts().get(i));
			}
		}
	}
}
