package apps.analsum;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import dal.TopologyType;
import helpers.gnuplot.GnuPlotResult;

public class SummaryTopologyReportGnuplot extends SummaryTopologyReportTemplate {

	private String filenameBase;
	private String xLabel;
	private String yLabel;

	private String domainHeader;
	private List<Double> domain;

	private List<String> dataHeaders;
	private List<Double> data;

	private int inGroup;

	@Override
	protected void onInit(SummaryTopologyResultAttributeSelector attributeSelector) {

		StringBuilder filenameBaseBuilder = new StringBuilder();

		filenameBaseBuilder.append(String.format("top_%s", attributeSelector.getName()));

		xLabel = "Topology size";
		yLabel = String.format("%s value", attributeSelector.getName());

		domainHeader = "N";
		domain = new ArrayList<>();

		dataHeaders = new ArrayList<>();
		data = new ArrayList<>();

		inGroup = 1;
	}

	@Override
	protected void onDataHeader(TopologyType topologyType) {
		dataHeaders.add(String.format("\"%s\"", topologyType.toString()));
		dataHeaders.add(String.format("CI"));
	}

	@Override
	protected void onDataHeaderDone() {
	}

	@Override
	protected void onDataRowBegin(int nodesCount) {
		domain.add((double) nodesCount);

	}

	@Override
	protected void onDataEmpty() {
		data.add(0.0);
		data.add(0.0);
	}

	@Override
	protected void onDataSingle(double mean) {
		data.add(mean);
		data.add(0.0);
	}

	@Override
	protected void onDataMultiple(double n, double mean, double confidenceIntervalWidth) {
		data.add(mean);
		data.add(confidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
	}

	@Override
	protected void onDone() {

		GnuplotUtils.createDirIfNotExists();

		try {
			PrintStream scriptWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.gp", GnuplotUtils.DIR_NAME, filenameBase)));
			PrintStream dataWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.txt", GnuplotUtils.DIR_NAME, filenameBase)));

			GnuPlotResult result = new GnuPlotResult(filenameBase, xLabel, yLabel, domainHeader, domain, dataHeaders,
					data, inGroup);

			result.writeGnuplotScript(scriptWriter);
			result.writeGnuplotData(dataWriter);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}
