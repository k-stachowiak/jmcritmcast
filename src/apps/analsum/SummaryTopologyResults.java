package apps.analsum;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.topanal.TopologyExperimentValues;

public class SummaryTopologyResults {
	public final SummaryStatistics degree = new SummaryStatistics();
	public final SummaryStatistics diameterHop = new SummaryStatistics();
	public final SummaryStatistics diameterCost = new SummaryStatistics();
	public final SummaryStatistics clusteringCoefficient = new SummaryStatistics();

	public void insert(TopologyExperimentValues experimentValues) {
		degree.addValue(experimentValues.getDegree());
		diameterHop.addValue(experimentValues.getDiameterHop());
		diameterCost.addValue(experimentValues.getDiameterCost());
		clusteringCoefficient.addValue(experimentValues
				.getClusteringCoefficient());
	}
}
