package apps.analsum.top;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import dal.TopologyType;

public abstract class SummaryTopologyReportTemplate {

	public void perform(SummaryTopologyResultTable resultsTable,
			SummaryTopologyResultAttributeSelector attributeSelector) {

		onInit(attributeSelector);
		
		for (TopologyType topologyType : TopologyType.values()) {
			onDataHeader(topologyType);
		}
		onDataHeaderDone();

		for (int nodesCount : CommonConfig.nodesCounts) {

			onDataRowBegin(nodesCount);
			Iterator<Entry<TopologyType, SummaryTopologyResults>> rowIterator = resultsTable.selectRow(nodesCount);

			while (rowIterator.hasNext()) {

				Entry<TopologyType, SummaryTopologyResults> entry = rowIterator.next();
				SummaryStatistics attribute = attributeSelector.select(entry.getValue());

				if (attribute.getN() == 0) {
					onDataEmpty();
				} else if (attribute.getN() == 1) {
					onDataSingle(attribute.getMean());
				} else {
					onDataMultiple(attribute.getN(), attribute.getMean(),
							SummaryUtils.getConfidenceIntervalWidth(attribute, CommonConfig.significance));
				}

			}

			onDataRowEnd();
		}

		onDone();
	}

	protected abstract void onInit(SummaryTopologyResultAttributeSelector attributeSelector);

	protected abstract void onDataHeader(TopologyType topologyType);

	protected abstract void onDataHeaderDone();

	protected abstract void onDataRowBegin(int nodesCount);

	protected abstract void onDataEmpty();

	protected abstract void onDataSingle(double mean);

	protected abstract void onDataMultiple(double n, double mean, double confidenceIntervalWidth);

	protected abstract void onDataRowEnd();

	protected abstract void onDone();
}
