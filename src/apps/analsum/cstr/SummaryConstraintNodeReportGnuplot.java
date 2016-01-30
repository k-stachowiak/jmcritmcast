package apps.analsum.cstr;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import apps.CommonConfig;
import apps.analsum.SummaryUtils;
import dal.TopologyType;
import helpers.gnuplot.GnuPlotRangeWriter;

public class SummaryConstraintNodeReportGnuplot
		extends SummaryConstraintNodeReportTemplate {

	private String filenameBase;
	private String xLabel;
	private String yLabel;

	private String domainHeader;
	private List<Double> domain;

	private List<String> dataHeaders;
	private List<Double> data;

	@Override
	protected void onInit(
			SummaryConstraintResultAttributeSelector attributeSelector,
			String groupperName, int nodesCount) {

		StringBuilder filenameBaseBuilder = new StringBuilder();

		filenameBaseBuilder.append(String.format("cstr_nd_%s_%d_%s",
				groupperName, nodesCount, attributeSelector.getName()));
		filenameBase = filenameBaseBuilder.toString();

		xLabel = "Graph size";
		yLabel = String.format("%s value", attributeSelector.getName());

		domainHeader = "N";
		domain = new ArrayList<>();

		dataHeaders = new ArrayList<>();
		data = new ArrayList<>();
	}

	@Override
	protected void onDataHeader(TopologyType topologyType) {
		dataHeaders
				.add(String.format("%s(mean_min)\t", topologyType.toString()));
		dataHeaders.add(String.format("%s(ci1)\t", topologyType.toString()));
		dataHeaders
				.add(String.format("%s(mean_max)\t", topologyType.toString()));
		dataHeaders.add(String.format("%s(ci2)\t", topologyType.toString()));
	}

	@Override
	protected void onDataHeaderDone() {
	}

	@Override
	protected void onDataRowBegin(int groupSize) {
		domain.add((double) groupSize);
	}

	@Override
	protected void onDataEmpty() {
		data.add(0.0);
		data.add(0.0);
		data.add(0.0);
		data.add(0.0);
	}

	@Override
	protected void onDataSingle(double minMean, double maxMean) {
		data.add(minMean);
		data.add(0.0);
		data.add(maxMean);
		data.add(0.0);
	}

	@Override
	protected void onDataMultiple(double n, double minMean,
			double minConfidenceIntervalWidth, double maxMean,
			double maxConfidenceIntervalWidth) {
		data.add(minMean);
		data.add(minConfidenceIntervalWidth);
		data.add(maxMean);
		data.add(maxConfidenceIntervalWidth);
	}

	@Override
	protected void onDataRowEnd() {
	}

	@Override
	protected void onDone() {

		SummaryUtils.createDirIfNotExists(CommonConfig.GNUPLOT_DIR_NAME);

		try {
			PrintStream scriptWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.gp",
							CommonConfig.GNUPLOT_DIR_NAME, filenameBase)));
			PrintStream dataWriter = new PrintStream(
					new FileOutputStream(String.format("%s/%s.txt",
							CommonConfig.GNUPLOT_DIR_NAME, filenameBase)));

			GnuPlotRangeWriter gnuplotWriter = new GnuPlotRangeWriter(
					filenameBase, xLabel, yLabel, domainHeader, domain,
					dataHeaders, data);

			gnuplotWriter.writeGnuplotScript(scriptWriter);
			gnuplotWriter.writeGnuplotData(dataWriter);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

}
