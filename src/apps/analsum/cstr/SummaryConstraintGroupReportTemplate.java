package apps.analsum.cstr;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import apps.constranal.ConstraintResultDataAccess;
import dal.TopologyType;

public abstract class SummaryConstraintGroupReportTemplate {

	public void perform(Connection connection, String groupperName, int groupSize, int metricIndex) {

		SummaryConstraintResultAttributeSelector attributeSelector = new SummaryConstraintResultAttributeSelector.Index(
				metricIndex);

		onInit(attributeSelector, groupperName, groupSize);

		for (TopologyType topologyType : TopologyType.values()) {
			onDataHeader(topologyType);
		}
		onDataHeaderDone();

		for (int nodesCount: CommonConfig.nodesCounts) {

			onDataRowBegin(nodesCount);

			Map<TopologyType, SummaryConstraintResults> row = ConstraintResultDataAccess
					.selectResults(connection, nodesCount, groupSize, groupperName);
			Iterator<Entry<TopologyType, SummaryConstraintResults>> it = row
					.entrySet().iterator();

			while (it.hasNext()) {

				Entry<TopologyType, SummaryConstraintResults> entry = it.next();
				SummaryConstraintResult attribute = attributeSelector
						.select(entry.getValue());

				if (attribute.getN() == 0) {
					onDataEmpty();
				} else if (attribute.getN() == 1) {
					onDataSingle(
							attribute.getMin().getMean(),
							attribute.getMax().getMean());
				} else {
					onDataMultiple(attribute.getN(),
							attribute.getMin().getMean(),
							SummaryUtils.getConfidenceIntervalWidth(
									attribute.getMin(),
									CommonConfig.significance),
							attribute.getMax().getMean(),
							SummaryUtils.getConfidenceIntervalWidth(
									attribute.getMax(),
									CommonConfig.significance));
				}
			}

			onDataRowEnd();
		}

		onDone();
	}

	protected abstract void onInit(
			SummaryConstraintResultAttributeSelector attributeSelector,
			String groupperName, int groupSize);

	protected abstract void onDataHeader(TopologyType topologyType);

	protected abstract void onDataHeaderDone();

	protected abstract void onDataRowBegin(int nodesCount);

	protected abstract void onDataEmpty();

	protected abstract void onDataSingle(double minMean, double maxMean);

	protected abstract void onDataMultiple(double n, double minMean,
			double minConfidenceIntervalWidth, double maxMean,
			double maxConfidenceIntervalWidth);

	protected abstract void onDataRowEnd();

	protected abstract void onDone();
}
