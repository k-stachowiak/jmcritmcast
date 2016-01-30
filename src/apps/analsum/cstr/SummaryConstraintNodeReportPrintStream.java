package apps.analsum.cstr;

import java.io.PrintStream;

import dal.TopologyType;

public class SummaryConstraintNodeReportPrintStream
		extends SummaryConstraintNodeReportTemplate {

	private final PrintStream out;

	public SummaryConstraintNodeReportPrintStream(PrintStream out) {
		this.out = out;
	}

	@Override
	protected void onInit(
			SummaryConstraintResultAttributeSelector attributeSelector,
			String groupperName, int nodesCount) {
		out.printf("%s, %d nodes, Attribute: %s", groupperName,
				nodesCount, attributeSelector.getName());
		out.println();
		out.print("M\t");
	}

	@Override
	protected void onDataHeader(TopologyType topologyType) {
		out.printf("%s(n)\t", topologyType.toString());
		out.printf("%s(mean1)\t", topologyType.toString());
		out.printf("%s(ci1)\t", topologyType.toString());
		out.printf("%s(mean2)\t", topologyType.toString());
		out.printf("%s(ci2)\t", topologyType.toString());
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
		out.print("0\t-\t-\t-\t-\t");
	}

	@Override
	protected void onDataSingle(double minMean, double maxMean) {
		out.printf("1\t%f\t-\t%f\t-\t", minMean, maxMean);
	}

	@Override
	protected void onDataMultiple(double n, double minMean,
			double minConfidenceIntervalWidth, double maxMean,
			double maxConfidenceIntervalWidth) {
		out.printf("%d\t%f\t%f\t%f\t%f\t", (int)n, minMean,
				minConfidenceIntervalWidth, maxMean,
				maxConfidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
		out.println();
	}

	@Override
	protected void onDone() {
	}

}