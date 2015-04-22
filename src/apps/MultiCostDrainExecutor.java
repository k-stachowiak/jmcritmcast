package apps;

import helpers.OspfCostResourceTranslation;
import helpers.TopologyAnalyser;
import helpers.gphmut.IndexResourceDrainer;
import helpers.gphmut.ResourceDrainer;
import helpers.metrprov.IndexMetricProvider;
import helpers.nodegrp.NodeGroupper;
import helpers.nodegrp.RandomNodeGroupper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import model.topology.Node;
import model.topology.Tree;
import tfind.SpanningTreeFinder;
import tfind.TreeFinderFactory;
import tfind.TreeFinderFactoryImpl;
import dal.DTOMarshaller;
import dto.ConstrainedTreeFindProblemDTO;
import dto.GraphDTO;

public class MultiCostDrainExecutor {

	// Factories.
	private static final GraphFactory graphFactory = new AdjacencyListFactory();
	private static final TreeFinderFactory treeFinderFactory = new TreeFinderFactoryImpl();
	private static final TopologyAnalyser topologyAnalyser = new TopologyAnalyser();
	private static final SpanningTreeFinder spanningTreeFinder = treeFinderFactory
			.createPrim(new IndexMetricProvider(0));

	private final ResourceDrainer resourceDrainer;
	private final NodeGroupper nodeGroupper;

	private final double drainedBandwidth;
	private final double minBandwidth;

	public MultiCostDrainExecutor(MultiCostDrainSetup setup, Random random) {
		resourceDrainer = new IndexResourceDrainer(
				new OspfCostResourceTranslation(setup.getBaseBandwidth()),
				setup.getDrainedIndex(), graphFactory);
		nodeGroupper = new RandomNodeGroupper(random);
		drainedBandwidth = setup.getDrainedBandwidth();
		minBandwidth = setup.getMinBandwidth();
	}

	public CostDrainResult execute(Graph graph, int groupSize,
			List<Double> constraints, String finderName) {

		boolean isFirstPass = true;
		Graph copy = graph.copy();
		int successCount = 0;
		List<Double> firstCosts = new ArrayList<>();

		// Drainage loop
		while (topologyAnalyser.isConnected(copy, spanningTreeFinder)
				&& copy.getNodes().size() >= groupSize) {

			List<Node> group = nodeGroupper.group(copy, groupSize);

			ConstrainedTreeFindProblemDTO problem = debugDumpCreate(graph, group, constraints, finderName);
			Tree tree = new ConstrainedTreeFindProblemSolver().solve(problem);
			debugDumpDestroy();
			
			if (tree == null) {
				break;
			}

			if (isFirstPass) {
				for (Double m : tree.getMetrics()) {
					firstCosts.add(m);
				}
				isFirstPass = false;
			}

			++successCount;

			copy = resourceDrainer.drain(copy, tree, drainedBandwidth,
					minBandwidth);
		}

		return new CostDrainResult(successCount, firstCosts);
	}
	
	private ConstrainedTreeFindProblemDTO debugDumpCreate(Graph graph, List<Node> group,
			List<Double> constraints, String finderName) {

		List<Integer> groupIds = new ArrayList<>();
		for (Node node : group) {
			groupIds.add(node.getId());
		}

		GraphDTO graphDto = GraphFactory.createDTO(graph);

		ConstrainedTreeFindProblemDTO problem = new ConstrainedTreeFindProblemDTO(
				graphDto, groupIds, constraints, finderName);

		DTOMarshaller<ConstrainedTreeFindProblemDTO> marshaller = new DTOMarshaller<>();

		File debugDataFile = new File("debug_data/current_problem.xml");
		String path = debugDataFile.getPath();
		marshaller.writeToFile(path, problem);
		
		return problem;
	}

	private void debugDumpDestroy() {
		File debugDataFile = new File("debug_data/current_problem.xml");
		if (debugDataFile.exists()) {
			debugDataFile.delete();
		}
	}
}
