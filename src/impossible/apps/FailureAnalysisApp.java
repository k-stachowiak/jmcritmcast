package impossible.apps;

import impossible.dal.DTOMarshaller;
import impossible.dto.ConstrainedTreeFindProblemDTO;
import impossible.dto.EdgeDTO;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.model.topology.AdjacencyListFactory;
import impossible.model.topology.Graph;
import impossible.model.topology.GraphFactory;
import impossible.model.topology.Node;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;
import impossible.pfnd.mlarac.ExpensiveNonBreakingPathSubstitutor;
import impossible.pfnd.mlarac.IntersectLambdaEstimator;
import impossible.pfnd.mlarac.LambdaEstimator;
import impossible.pfnd.mlarac.PathSubstiutor;
import impossible.tfind.MetricConstrainedSteinerTreeFinder;
import impossible.tfind.SpanningTreeFinder;
import impossible.tfind.TreeFinderFactory;
import impossible.tfind.TreeFinderFactoryImpl;
import impossible.tfind.prim.PrimTreeFinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FailureAnalysisApp {

	private static final String DRAINAGE_DIR = "selected_drainagefail";

	public static void main(String[] args) {

		File problemsDir = new File(DRAINAGE_DIR);
		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();
		for (File problemFile : problemsDir.listFiles()) {

			// Skip non-XML files.
			int pos = problemFile.getName().lastIndexOf('.');
			String ext = problemFile.getName().substring(pos + 1);
			if (ext.compareTo("xml") != 0) {
				continue;
			}

			// Process file.
			ConstrainedTreeFindProblemDTO problem = marshaller.readFromFile(
					problemFile.getPath(), ConstrainedTreeFindProblemDTO.class);

			analyzeProblem(problem);
		}
	}

	private static void analyzeProblem(ConstrainedTreeFindProblemDTO problem) {

		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();
		SpanningTreeFinder spanningTreeFinder = new PrimTreeFinder(
				new IndexMetricProvider(0));

		// Convert problem data to logic domain.
		Graph graph = graphFactory.createFromDTO(problem.getGraph());

		List<Double> constraints = problem.getConstraints();

		MetricConstrainedSteinerTreeFinder finder = finderFromName(
				problem.getFinderName(), spanningTreeFinder, pathFinderFactory,
				treeFinderFactory);

		List<Node> group = new ArrayList<>();
		for (Integer id : problem.getGroup()) {
			group.add(graph.getNode(id));
		}

		// Provide debugging info.
		generateGraphimage(problem);

		// Run the problematic case.
		finder.find(graph, group, constraints);
	}

	private static void generateGraphimage(ConstrainedTreeFindProblemDTO problem) {

		try {

			File out = new File(DRAINAGE_DIR + "/" + problem.hashCode()
					+ ".dot");
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(out));

			writer.write("digraph G {");
			writer.newLine();

			for (EdgeDTO edge : problem.getGraph().getEdges()) {

				StringBuilder metricsString = new StringBuilder();
				for (int i = 0; i < edge.getMetrics().size(); ++i) {
					double metric = edge.getMetrics().get(i);
					metricsString.append(String.format("%1$.2f", metric));
					if (i < edge.getMetrics().size() - 1) {
						metricsString.append(", ");
					}
				}

				String edgeLine = String.format(
						"%1$d -> %2$d [label=\"%3$s\"];", edge.getNodeFrom(),
						edge.getNodeTo(), metricsString.toString());

				writer.write(edgeLine);
				writer.newLine();
			}

			writer.write("}");
			writer.newLine();

			writer.close();

		} catch (IOException exception) {
			// Ignore...
		}
	}

	private static MetricConstrainedSteinerTreeFinder finderFromName(
			String finderName, SpanningTreeFinder spanningTreeFinder,
			PathFinderFactory pathFinderFactory,
			TreeFinderFactory treeFinderFactory) {

		// Common strategies.
		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();

		// Specific finders allocation.

		if (finderName.equals("HMCMC")) {
			return treeFinderFactory.createHmcmc(constraintsComparer,
					pathFinderFactory, pathAggregator);
		}

		if (finderName.equals("AGGR_MLARAC")) {
			PathSubstiutor pathSubstitutor = new ExpensiveNonBreakingPathSubstitutor();
			LambdaEstimator lambdaEstimator = new IntersectLambdaEstimator();
			ConstrainedPathFinder mlarac = pathFinderFactory.createMlarac(
					pathSubstitutor, lambdaEstimator, constraintsComparer);
			return treeFinderFactory.createConstrainedPathAggr(mlarac,
					pathAggregator);
		}

		if (finderName.equals("AGGR_LBPSA")) {
			ConstrainedPathFinder lbpsa = pathFinderFactory
					.createLbpsa(constraintsComparer);
			return treeFinderFactory.createConstrainedPathAggr(lbpsa,
					pathAggregator);
		}

		if (finderName.equals("AGGR_HMCOP")) {
			double lambda = Double.POSITIVE_INFINITY;
			ConstrainedPathFinder hmcop = pathFinderFactory.createHmcop(lambda);
			return treeFinderFactory.createConstrainedPathAggr(hmcop,
					pathAggregator);
		}

		if (finderName.equals("RDP_QE")) {
			return treeFinderFactory.createRdpQuasiExact(constraintsComparer);
		}
		
		if (finderName.equals("RDP_H")) {
			return treeFinderFactory.createRdpHeuristic(constraintsComparer);
		}

		return null;
	}
}
