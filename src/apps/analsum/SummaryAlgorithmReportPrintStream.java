package apps.analsum;

import java.io.PrintStream;
import java.util.List;

import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;
import tfind.TreeFinderType;

public class SummaryAlgorithmReportPrintStream extends SummaryAlgorithmReportTemplate {

	private final PrintStream out;

	public SummaryAlgorithmReportPrintStream(PrintStream out) {
		this.out = out;
	}

	@Override
	protected void onInit(TopologyType topologyType, int nodesCount, NodeGroupperType nodeGroupperType,
			double constraintBase, List<SummaryAlgorithmResultAttributeSelector> attributeSelectors) {

		out.printf("Topology: %s, Nodes count: %d, Groupper: %s, Constraint base: %f, Attributes: ",
				topologyType.toString(), nodesCount, nodeGroupperType.toString(), constraintBase);

		for (SummaryAlgorithmResultAttributeSelector attributeSelector : attributeSelectors) {
			out.printf(" %s", attributeSelector.getName());
		}
		out.println();

		out.print("M\t");
	}

	@Override
	protected void onDataHeader(SummaryAlgorithmResultAttributeSelector attributeSelector,
			TreeFinderType treeFinderType) {
		out.printf("\"%s_{%s}(N)\"\t", treeFinderType.toString(), attributeSelector.getName());
		out.printf("\"%s_{%s}\"\t", treeFinderType.toString(), attributeSelector.getName());
		out.printf("\"%s_{%s}(CI)\"\t", treeFinderType.toString(), attributeSelector.getName());
	}

	@Override
	protected void onDataHeaderDone() {
		out.println();
	}

	@Override
	protected void onDataRowBegin(int groupSize) {
		out.printf("%d\t", groupSize);
	}

	@Override
	protected void onDataEmpty() {
		out.print("0\t-\t-\t");
	}

	@Override
	protected void onDataSingle(double mean) {
		out.printf("1\t%f\t-\t", mean);
	}

	@Override
	protected void onDataMultiple(double n, double mean, double confidenceIntervalWidth) {
		out.printf("%d\t%f\t%f\t", (int) n, mean, confidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
		out.println();
	}

	@Override
	protected void onDone() {
	}
}
