package apps.analsum.group;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import dal.TopologyType;
import helpers.gnuplot.GnuPlotLines;
import helpers.nodegrp.NodeGroupperType;

public class SummaryGroupReportGraphSizeGnuplot extends SummaryGroupReportGraphSizeTemplate {

	private String filenameBase;
	private String xLabel;
	private String yLabel;

	private String domainHeader;
	private List<Double> domain;

	private List<String> dataHeaders;
	private List<Double> data;

	private int inGroup;

	@Override
	protected void onInit(SummaryGroupResultAttributeSelector attributeSelector, TopologyType topologyType,
			int groupSize) {
		StringBuilder filenameBaseBuilder = new StringBuilder();

		filenameBaseBuilder.append(
				String.format("grp_gph_%s_%d_%s", topologyType.toString(), groupSize, attributeSelector.getName()));
		filenameBase = filenameBaseBuilder.toString();

		xLabel = "Graph nodes count";
		yLabel = String.format("%s value", attributeSelector.getName());

		domainHeader = "N";
		domain = new ArrayList<>();

		dataHeaders = new ArrayList<>();
		data = new ArrayList<>();

		inGroup = 1;
	}

	@Override
	protected void onDataHeader(NodeGroupperType nodeGroupperType) {
		dataHeaders.add(String.format("\"%s\"", nodeGroupperType.toString()));
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
	protected void onDataMultiple(long n, double mean, double confidenceIntervalWidth) {
		data.add(mean);
		data.add(confidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
	}

	@Override
	protected void onDone() {

		SummaryUtils.createDirIfNotExists(CommonConfig.GNUPLOT_DIR_NAME);

		try {
			PrintStream scriptWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.gp", CommonConfig.GNUPLOT_DIR_NAME, filenameBase)));
			PrintStream dataWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.txt", CommonConfig.GNUPLOT_DIR_NAME, filenameBase)));

			GnuPlotLines result = new GnuPlotLines(filenameBase, xLabel, yLabel, domainHeader, domain, dataHeaders,
					data, inGroup);

			result.writeGnuplotScript(scriptWriter);
			result.writeGnuplotData(dataWriter);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}
