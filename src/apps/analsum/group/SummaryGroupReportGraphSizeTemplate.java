package apps.analsum.group;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;

public abstract class SummaryGroupReportGraphSizeTemplate {

	public void perform(SummaryGroupResultGraphSizeTable resultsTable,
			SummaryGroupResultAttributeSelector attributeSelector) {

		for (TopologyType topologyType : TopologyType.values()) {
			for (int groupSize : CommonConfig.groupSizes) {

				onInit(attributeSelector, topologyType, groupSize);

				for (NodeGroupperType nodeGroupperType : NodeGroupperType.values()) {
					onDataHeader(nodeGroupperType);
				}
				onDataHeaderDone();

				for (int nodesCount : CommonConfig.nodesCounts) {

					onDataRowBegin(nodesCount);
					Iterator<Entry<NodeGroupperType, SummaryGroupResults>> rowIterator = resultsTable
							.selectRow(topologyType, groupSize, nodesCount);

					while (rowIterator.hasNext()) {
						Entry<NodeGroupperType, SummaryGroupResults> entry = rowIterator.next();
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
		}

	}

	protected abstract void onInit(SummaryGroupResultAttributeSelector attributeSelector, TopologyType topologyType,
			int groupSize);

	protected abstract void onDataHeader(NodeGroupperType nodeGroupperType);

	protected abstract void onDataHeaderDone();

	protected abstract void onDataRowBegin(int nodesCount);

	protected abstract void onDataEmpty();

	protected abstract void onDataSingle(double mean);

	protected abstract void onDataMultiple(long n, double mean, double confidenceIntervalWidth);

	protected abstract void onDataRowEnd();

	protected abstract void onDone();

}