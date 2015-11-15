package apps.analsum.top;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.topanal.TopologyExperimentValues;

public class SummaryTopologyResults {
	public final SummaryStatistics degree = new SummaryStatistics();
	public final SummaryStatistics diameterHop = new SummaryStatistics();
	public final SummaryStatistics diameterCost = new SummaryStatistics();
	public final SummaryStatistics clusteringCoefficient = new SummaryStatistics();

	public void insert(TopologyExperimentValues experimentValues) {

		Double maybeDegree = experimentValues.getDegree();
		if (maybeDegree != null) {
			degree.addValue(maybeDegree);
		}

		Double maybeDiameterHop = experimentValues.getDiameterHop();
		if (maybeDiameterHop != null) {
			diameterHop.addValue(maybeDiameterHop);
		}

		Double maybeDiameterCost = experimentValues.getDiameterCost();
		if (maybeDiameterCost != null) {
			diameterCost.addValue(maybeDiameterCost);
		}

		Double maybeClusteringCoefficient = experimentValues.getClusteringCoefficient();
		if (maybeClusteringCoefficient != null) {
			clusteringCoefficient.addValue(maybeClusteringCoefficient);
		}
	}
}
