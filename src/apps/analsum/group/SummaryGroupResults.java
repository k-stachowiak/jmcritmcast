package apps.analsum.group;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.groupanal.GroupExperimentValues;

public class SummaryGroupResults {
	public final SummaryStatistics degree = new SummaryStatistics();
	public final SummaryStatistics diameterHop = new SummaryStatistics();
	public final SummaryStatistics diameterCost = new SummaryStatistics();
	public final SummaryStatistics clusteringCoefficient = new SummaryStatistics();
	public final SummaryStatistics density = new SummaryStatistics();

	public void insert(GroupExperimentValues experimentValues) {
		degree.addValue(experimentValues.getDegree());
		diameterHop.addValue(experimentValues.getDiameterHop());
		diameterCost.addValue(experimentValues.getDiameterCost());
		clusteringCoefficient.addValue(experimentValues
				.getClusteringCoefficient());
		density.addValue(experimentValues.getDensity());
	}

}
