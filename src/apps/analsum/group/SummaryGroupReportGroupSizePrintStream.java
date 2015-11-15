package apps.analsum.group;

import java.io.PrintStream;

import dal.TopologyType;
import helpers.nodegrp.NodeGroupperType;

public class SummaryGroupReportGroupSizePrintStream extends SummaryGroupReportGroupSizeTemplate {

	private final PrintStream out;

	public SummaryGroupReportGroupSizePrintStream(PrintStream out) {
		this.out = out;
	}

	@Override
	protected void onInit(SummaryGroupResultAttributeSelector attributeSelector, TopologyType topologyType,
			int nodesCount) {
		out.printf("Attribute: %s, Topology: %s, Nodes count: %d", attributeSelector.getName(), topologyType.toString(),
				nodesCount);
		out.println();

		out.print("M\t");
	}

	@Override
	protected void onDataHeader(NodeGroupperType nodeGroupperType) {
		out.printf("%s(n)\t", nodeGroupperType.toString());
		out.printf("%s(mean)\t", nodeGroupperType.toString());
		out.printf("%s(ci)\t", nodeGroupperType.toString());
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
	protected void onDataMultiple(long n, double mean, double confidenceIntervalWidth) {
		out.printf("%d\t%f\t%f\t", n, mean, confidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
		out.println();
	}

	@Override
	protected void onDone() {
	}

}
