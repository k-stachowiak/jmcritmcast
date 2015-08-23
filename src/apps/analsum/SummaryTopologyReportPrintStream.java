package apps.analsum;

import java.io.PrintStream;

import dal.TopologyType;

public class SummaryTopologyReportPrintStream extends SummaryTopologyReportTemplate {

	private final PrintStream out;

	public SummaryTopologyReportPrintStream(PrintStream out) {
		this.out = out;
	}

	@Override
	protected void onInit(SummaryTopologyResultAttributeSelector attributeSelector) {
		out.printf("Attribute: %s", attributeSelector.getName());
		out.println();

		out.print("N\t");
	}
	
	@Override
	protected void onDataHeader(TopologyType topologyType)
	{
		out.printf("%s(n)\t", topologyType.toString());
		out.printf("%s(mean)\t", topologyType.toString());
		out.printf("%s(ci)\t", topologyType.toString());
	}

	@Override
	protected void onDataHeaderDone() {
		out.println();
	}

	@Override
	protected void onDataRowBegin(int nodesCount) {
		out.printf("%d\t", nodesCount);
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
