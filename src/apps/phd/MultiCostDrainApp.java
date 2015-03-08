package apps.phd;

import helpers.gphmut.MetricRedistribution;
import helpers.gphmut.MetricRedistributionImpl;
import helpers.gphmut.UniformDistributionParameters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import util.TimeMeasurement;
import dal.InputGraphStreamer;
import dal.MPiechGraphStreamer;
import dto.GraphDTO;

public class MultiCostDrainApp {

	public static void main(String[] args) {

		Locale.setDefault(Locale.ENGLISH);

		final MultiCostDrainSetup setup = readConfig("config");
		if (setup == null) {
			System.err.println("Error in configuration file.");
			return;
		}

		final Random random = new Random(setup.getRandomSeed());

		final MultiCostDrainExecutor exec = new MultiCostDrainExecutor(setup,
				random);

		forEachCase(setup, random, exec);
	}

	private static MultiCostDrainSetup readConfig(String filename) {

		final long randomSeed;
		final List<List<Double>> constraintCases;
		final double baseBandwidth;
		final double drainedBandwidth;
		final double minBandwidth;
		final int drainedIndex;
		final String topologiesDirecotry;
		final String topology;
		final int graphsInFile;
		final double redistributionMin;
		final double redistributionMax;
		final int graphs;
		final List<Integer> nodeSizes;
		final List<Integer> criteriaCounts;
		final List<Integer> groupSizes;
		final List<String> algNames;

		Properties properties = new Properties();
		FileInputStream fis;

		// Read and parse configuration file.
		// ---------------------------
		try {
			fis = new FileInputStream(filename);
			properties.load(fis);

		} catch (FileNotFoundException e) {
			System.err.println("Exception: Configuration file not found.");
			return null;

		} catch (IOException e) {
			System.err.println("Exception: Error loading configuration file.");
			return null;

		}

		// Peal out numeric values and collections.
		// ----------------------------------------
		try {
			randomSeed = Long.parseLong(properties.getProperty("randomSeed"));
			constraintCases = parseConstraintCases(properties
					.getProperty("constraintCases"));
			baseBandwidth = Double.parseDouble(properties
					.getProperty("baseBandwidth"));
			drainedBandwidth = Double.parseDouble(properties
					.getProperty("drainedBandwidth"));
			minBandwidth = Double.parseDouble(properties
					.getProperty("minBandwidth"));
			drainedIndex = Integer.parseInt(properties
					.getProperty("drainedIndex"));
			graphsInFile = Integer.parseInt(properties
					.getProperty("graphsInFile"));
			redistributionMin = Double.parseDouble(properties
					.getProperty("redistributionMin"));
			redistributionMax = Double.parseDouble(properties
					.getProperty("redistributionMax"));
			graphs = Integer.parseInt(properties.getProperty("graphs"));

			String nssStr = properties.getProperty("nodeSizes");
			String[] nss = nssStr.split(",");
			nodeSizes = new ArrayList<>();
			for (String ns : nss) {
				nodeSizes.add(Integer.parseInt(ns));
			}

			String ccsStr = properties.getProperty("criteriaCounts");
			String[] ccs = ccsStr.split(",");
			criteriaCounts = new ArrayList<>();
			for (String cc : ccs) {
				criteriaCounts.add(Integer.parseInt(cc));
			}

			String gssStr = properties.getProperty("groupSizes");
			String[] gss = gssStr.split(",");
			groupSizes = new ArrayList<>();
			for (String gs : gss) {
				groupSizes.add(Integer.parseInt(gs));
			}

		} catch (NumberFormatException ex) {
			System.err.println("Exception: Parameter parsing error \""
					+ ex.getMessage() + "\"");
			return null;

		}

		// Read the string properties.
		// ---------------------------
		topologiesDirecotry = properties.getProperty("topologiesDirectory");
		topology = properties.getProperty("topology");
		String ansStr = properties.getProperty("algNames");

		if (topologiesDirecotry == null) {
			System.err
					.println("Exception: Error while parsing topologies directory property.");
			return null;
		}

		if (topology == null) {
			System.err
					.println("Exception: Error while parsing topology property.");
			return null;
		}

		if (ansStr == null) {
			System.err
					.println("Exception: Error while parsing algorithm names property.");
			return null;
		}

		algNames = new ArrayList<>(Arrays.asList(ansStr.split(",")));

		return new MultiCostDrainSetup(randomSeed, constraintCases,
				baseBandwidth, drainedBandwidth, minBandwidth, drainedIndex,
				graphs, nodeSizes, criteriaCounts, groupSizes,
				topologiesDirecotry, topology, graphsInFile, redistributionMin,
				redistributionMax, algNames);
	}

	private static List<List<Double>> parseConstraintCases(String property) {

		List<List<Double>> result = new ArrayList<>();

		for (String constraintSetStr : property.split(";")) {
			List<Double> constraintSet = new ArrayList<>();
			for (String constraint : constraintSetStr.split("\\s+")) {
				constraintSet.add(Double.parseDouble(constraint));
			}
			result.add(constraintSet);
		}

		return result;
	}

	private static void forEachCase(MultiCostDrainSetup setup, Random random,
			MultiCostDrainExecutor exec) {

		final TimeMeasurement timeMeasurement = new TimeMeasurement();
		final StringBuilder resultString = new StringBuilder();
		final PrintWriter debugWriter = new PrintWriter(System.err, true);
		final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM HH:mm:ss");

		final Map<Integer, String> streamerNames = new HashMap<>();
		streamerNames.put(100, "n_100_W___1000");
		streamerNames.put(200, "n_200_W___1000");
		streamerNames.put(300, "n_300_W___1000");
		streamerNames.put(400, "n_400_W___1000");
		streamerNames.put(500, "n_500_W___1000");
		streamerNames.put(1000, "n_1000_W___1000");

		final GraphFactory graphFactory = new AdjacencyListFactory();
		final MetricRedistribution metricRedistribution = new MetricRedistributionImpl(
				graphFactory, random);

		for (Integer criteriaCount : setup.getCriteriaCounts()) {
			String critString = "crit = " + criteriaCount;

			for (Integer groupSize : setup.getGroupSizes()) {
				String critGroupString = critString + " g = " + groupSize;

				for (List<Double> constraints : setup.GetConstraintCases()) {

					List<Double> constraintsCopy = new ArrayList<>(constraints);
					int constraintsSize = criteriaCount - 1;
					if (constraintsCopy.size() > (constraintsSize)) {
						constraintsCopy.subList(constraintsSize,
								constraintsCopy.size()).clear();
					}

					String critGroupConstrString = critGroupString + " cstr = "
							+ toString(constraintsCopy, ",");

					for (String finderName : setup.getTreeFinderNames()) {
						String critGroupConstrFndString = critGroupConstrString
								+ " alg = " + finderName;

						for (Integer nodeSize : setup.getNodeSizes()) {

							String critGroupConstrFndNodeString = critGroupConstrFndString
									+ " n = " + nodeSize.toString();

							while (!streamerNames.isEmpty()) {
								String topName = streamerNames.get(nodeSize);
								String problemString = sdf.format(new Date())
										+ " " + critGroupConstrFndNodeString
										+ " top = " + topName;

								debugWriter.print(problemString);
								debugWriter.flush();

								timeMeasurement.begin();

								final InputGraphStreamer inputGraphStreamer = prepareGraphStreamer(
										setup, topName);
								if (inputGraphStreamer == null) {
									throw new RuntimeException(
											"Failed opening graph streamer.\n");
								}

								StringBuilder partialResultStringBuilder = new StringBuilder();

								List<UniformDistributionParameters> parameters = new ArrayList<>();
								for (int p = 0; p < criteriaCount; ++p)
									parameters
											.add(new UniformDistributionParameters(
													setup.getRedistributionMin(),
													setup.getRedistributionMax()));

								for (int g = 0; g < setup.getGraphs(); ++g) {

									GraphDTO graphDTO = inputGraphStreamer
											.getNext();
									Graph graph = graphFactory
											.createFromDTO(graphDTO);
									graphDTO = null;

									graph = metricRedistribution.redistUniform(
											graph, parameters);

									CostDrainResult result = exec.execute(
											graph, groupSize, constraints,
											finderName);

									partialResultStringBuilder.append(topName);
									partialResultStringBuilder.append('\t');

									partialResultStringBuilder
											.append(finderName);
									partialResultStringBuilder.append('\t');

									partialResultStringBuilder
											.append(criteriaCount);
									partialResultStringBuilder.append('\t');

									partialResultStringBuilder.append(toString(
											constraints, ","));
									partialResultStringBuilder.append('\t');

									partialResultStringBuilder
											.append(groupSize);
									partialResultStringBuilder.append('\t');

									partialResultStringBuilder.append(result
											.getSuccessCount());
									partialResultStringBuilder.append('\t');

									List<Double> firstCosts = result
											.getFirstCosts();
									for (int i = 0; i < firstCosts.size(); ++i) {
										partialResultStringBuilder
												.append(firstCosts.get(i));
										if (i < (firstCosts.size() - 1))
											partialResultStringBuilder
													.append('\t');
									}

									partialResultStringBuilder.append('\n');
								}

								timeMeasurement.end();

								debugWriter.println(" Elapsed : "
										+ timeMeasurement.getDurationString());

								resultString.append(partialResultStringBuilder);

								streamerNames.remove(streamerNames.size() - 1);
							}
						}
					}
				}
			}
		}

		PrintWriter outWriter = new PrintWriter(System.out, true);
		outWriter.print(resultString.toString());
		outWriter.close();

		debugWriter.println("Terminated normally");

		debugWriter.close();

	}

	private static InputGraphStreamer prepareGraphStreamer(
			MultiCostDrainSetup setup, String topName) {

		String edgesFilename = setup.getTopologiesDirectory() + "/edges_"
				+ topName + ".txt";
		String nodesFilename = setup.getTopologiesDirectory() + "/nodes_"
				+ topName + ".txt";

		BufferedReader edgesReader = null;
		BufferedReader nodesReader = null;

		try {
			edgesReader = new BufferedReader(new FileReader(edgesFilename));
			nodesReader = new BufferedReader(new FileReader(nodesFilename));
		} catch (FileNotFoundException exception) {
			return null;
		}

		InputGraphStreamer inputGraphStreamer = new MPiechGraphStreamer(200,
				nodesReader, edgesReader);

		return inputGraphStreamer;
	}

	private static String toString(List<Double> values, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.size(); ++i) {
			sb.append(values.get(i));
			if (i < (values.size() - 1)) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
