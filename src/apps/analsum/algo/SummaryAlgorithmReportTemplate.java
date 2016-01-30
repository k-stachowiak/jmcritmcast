package apps.analsum.algo;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;
import tfind.TreeFinderType;

public abstract class SummaryAlgorithmReportTemplate {

	public void perform(SummaryAlgorithmResultTable resultsTable,
			List<SummaryAlgorithmResultAttributeSelector> attributeSelectors) {

		for (TopologyType topologyType : TopologyType.values()) {
			for (int nodesCount : CommonConfig.nodesCounts) {
				for (NodeGroupperType nodeGroupperType : NodeGroupperType.values()) {
					for (List<Double> constraints : CommonConfig.constraintSets) {

						// Handle basic initialization here
						onInit(topologyType, nodesCount, nodeGroupperType, constraints, attributeSelectors);

						// Header related data
						for (SummaryAlgorithmResultAttributeSelector attributeSelector : attributeSelectors) {
							for (TreeFinderType treeFinderType : TreeFinderType.values()) {
								onDataHeader(attributeSelector, treeFinderType);
							}
						}
						onDataHeaderDone();

						// Sweep the domain
						for (int groupSize : CommonConfig.groupSizes) {

							onDataRowBegin(groupSize);

							Iterator<Entry<TreeFinderType, SummaryAlgorithmResults>> rowIterator = resultsTable
									.selectRow(topologyType, nodesCount, groupSize, nodeGroupperType,
											constraints.get(0), constraints.get(1));

							while (rowIterator.hasNext()) {
								Entry<TreeFinderType, SummaryAlgorithmResults> entry = rowIterator.next();

								for (SummaryAlgorithmResultAttributeSelector attributeSelector : attributeSelectors) {
									SummaryStatistics attribute = attributeSelector.select(entry.getValue());

									if (attribute.getN() == 0) {
										onDataEmpty();
									} else if (attribute.getN() == 1) {
										onDataSingle(attribute.getMean());
									} else {
										onDataMultiple(attribute.getN(), attribute.getMean(), SummaryUtils
												.getConfidenceIntervalWidth(attribute, CommonConfig.significance));
									}
								}
							}

							onDataRowEnd();
						}

						onDone();
					}
				}
			}
		}
	}

	protected String constraintsString(List<Double> constraints) {
		StringBuilder result = new StringBuilder();
		result.append(constraints.get(0).toString());
		for (int i = 1; i < constraints.size(); ++i) {
			result.append('-');
			result.append(constraints.get(i).intValue());
		}
		return result.toString();
	}

	protected abstract void onInit(TopologyType topologyType, int nodesCount, NodeGroupperType nodeGroupperType,
			List<Double> constraints, List<SummaryAlgorithmResultAttributeSelector> attributeSelectors);

	protected abstract void onDataHeader(SummaryAlgorithmResultAttributeSelector attributeSelector,
			TreeFinderType treeFinderType);

	protected abstract void onDataHeaderDone();

	protected abstract void onDataRowBegin(int groupSize);

	protected abstract void onDataEmpty();

	protected abstract void onDataSingle(double mean);

	protected abstract void onDataMultiple(double n, double mean, double confidenceIntervalWidth);

	protected abstract void onDataRowEnd();

	protected abstract void onDone();
}
