package impossible.apps;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.hlanalysis.MultiDrain;
import impossible.hlanalysis.MultiDrainSetup;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import impossible.pfnd.mlarac.IntersectLambdaEstimator;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;
import impossible.tfind.ConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MultiDrainApp {

	static long randomSeed;
	static double fengDelta;
	static double baseBandwidth;
	static double drainedBandwidth;
	static String topologiesDirecotry;
	static String topology;
	static int graphsInFile;
	static double redistributionMin;
	static double redistributionMax;
	static int graphs;
	static List<Integer> nodeSizes;
	static List<Integer> criteriaCounts;
	static List<Integer> groupSizes;
	static List<String> algNames;

	private static boolean readConfig() {

		Properties properties = new Properties();
		FileInputStream fis;

		// Read and parse config file.
		// ---------------------------
		try {
			fis = new FileInputStream("config");
			properties.load(fis);

		} catch (FileNotFoundException e) {
			System.err.println("Exception: Configuration file not found.");
			return false;

		} catch (IOException e) {
			System.err.println("Exception: Error loading configuration file.");
			return false;

		}

		// Peal out numeric values and collections.
		// ----------------------------------------
		try {
			randomSeed = Long.parseLong(properties.getProperty("randomSeed"));
			fengDelta = Double.parseDouble(properties.getProperty("fengDelta"));
			baseBandwidth = Double.parseDouble(properties
					.getProperty("baseBandwidth"));
			drainedBandwidth = Double.parseDouble(properties
					.getProperty("drainedBandwidth"));
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
			for (String ns : nss)
				nodeSizes.add(Integer.parseInt(ns));

			String ccsStr = properties.getProperty("criteriaCounts");
			String[] ccs = ccsStr.split(",");
			criteriaCounts = new ArrayList<>();
			for (String cc : ccs)
				criteriaCounts.add(Integer.parseInt(cc));

			String gssStr = properties.getProperty("groupSizes");
			String[] gss = gssStr.split(",");
			groupSizes = new ArrayList<>();
			for (String gs : gss)
				groupSizes.add(Integer.parseInt(gs));

		} catch (NumberFormatException ex) {
			System.err.println("Exception: Parameter parsing error \""
					+ ex.getMessage() + "\"");
			return false;

		}

		// Read the string properties.
		// ---------------------------
		topologiesDirecotry = properties.getProperty("topologiesDirectory");
		topology = properties.getProperty("topology");
		String ansStr = properties.getProperty("algNames");

		if (topologiesDirecotry == null) {
			System.err
					.println("Exception: Error while parsing topologies directory property.");
			return false;
		}

		if (topology == null) {
			System.err
					.println("Exception: Error while parsing topology property.");
			return false;
		}

		if (ansStr == null) {
			System.err
					.println("Exception: Error while parsing algorithm names property.");
			return false;
		}

		algNames = new ArrayList<>(Arrays.asList(ansStr.split(",")));

		return true;
	}

	private static Map<String, ConstrainedSteinerTreeFinder> initializeAllTreeFinders() {

		// Factories.
		// ----------
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();

		// Strategies.
		// -----------
		MetricProvider metricProvider = new IndexMetricProvider(0);

		SpanningTreeFinder spanningTreeFinder = treeFinderFactory
				.createPrim(metricProvider);

		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// Helper MLARAC path finder.
		// --------------------------
		PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();

		LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();

		ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(null,
				pathSubstitutor, lambdaEstimator, constraintsComparer);

		ConstrainedPathFinder lbpsa = pathFinderFactory.createLbpsa(
				constraintsComparer, null);

		// Build the result.
		// -----------------
		Map<String, ConstrainedSteinerTreeFinder> treeFinders = new HashMap<>();

		treeFinders.put("HMCMC", treeFinderFactory.createHmcmc(
				constraintsComparer, pathFinderFactory, pathAggregator, null));

		treeFinders.put("AGGR_MLARAC", treeFinderFactory
				.createConstrainedPathAggr(mlarac, pathAggregator));

		treeFinders.put("AGGR_LBPSA", treeFinderFactory
				.createConstrainedPathAggr(lbpsa, pathAggregator));

		return treeFinders;
	}

	public static void main(String[] args) {

		// Parse the config file.
		// ----------------------
		if (!readConfig()) {
			System.err.println("Error in configuration file.");
			return;
		}

		// Generate and filter the finders list.
		// -------------------------------------
		Map<String, ConstrainedSteinerTreeFinder> treeFinders = initializeAllTreeFinders();
		Map<String, ConstrainedSteinerTreeFinder> filteredTreeFinders = new HashMap<>();
		for (Map.Entry<String, ConstrainedSteinerTreeFinder> entry : treeFinders
				.entrySet()) {
			if (algNames.contains(entry.getKey()))
				filteredTreeFinders.put(entry.getKey(), entry.getValue());
		}

		// Build a setup definition for the application and run it.
		// --------------------------------------------------------
		final MultiDrainSetup setup = new MultiDrainSetup(randomSeed,
				fengDelta, baseBandwidth, drainedBandwidth, graphs, nodeSizes,
				criteriaCounts, groupSizes, topologiesDirecotry, topology,
				graphsInFile, redistributionMin, redistributionMax,
				filteredTreeFinders);

		new MultiDrain(setup).run(args, System.out);
	}

}
