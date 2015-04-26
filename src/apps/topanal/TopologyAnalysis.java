package apps.topanal;

import helpers.TopologyAnalyser;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import apps.topanal.data.TopologyAnalysisCase;
import apps.topanal.data.TopologyAnalysisMacroResult;
import apps.topanal.data.TopologyType;
import model.topology.AdjacencyListFactory;
import model.topology.Graph;
import model.topology.GraphFactory;
import dal.MultiBriteGraphStreamer;
import dto.GraphDTO;

public class TopologyAnalysis implements TopologyAnalysisExecutor {

	private final GraphFactory graphFactory = new AdjacencyListFactory();

	private static final List<Integer> nodesCounts = Arrays
			.asList(new Integer[] { 50, 100, 250, 500, 1500, 3037, 3600, 4750,
					6000 });

	private static void forEachCase(TopologyAnalysisExecutor executor, int graphsCount) {
		for (TopologyType type : TopologyType.values()) {
			for (Integer nodesCount : nodesCounts) {
				executor.execute(new TopologyAnalysisCase(type, nodesCount, graphsCount));
			}
		}
	}

	public static void main(String[] args) {
		forEachCase(new TopologyAnalysis(), 10);
	}

	@Override
	public void execute(TopologyAnalysisCase tac) {

		MultiBriteGraphStreamer gs = new MultiBriteGraphStreamer("data/phd",
				tac.getType(), tac.getNodesCount(), 100);

		SummaryStatistics degreeStat = new SummaryStatistics();
		SummaryStatistics diameterStat = new SummaryStatistics();
		SummaryStatistics clusteringStat = new SummaryStatistics();
		
		TopologyAnalysisCase.printHeader(System.out);
		TopologyAnalysisMacroResult.printHeader(System.out);

		while (gs.hasNext()) {
			GraphDTO graphDTO = gs.getNext();
			Graph graph = graphFactory.createFromDTO(graphDTO);
			graphDTO = null;
			degreeStat.addValue(TopologyAnalyser.averageDegree(graph));
			diameterStat.addValue(TopologyAnalyser.diameter(graph));
			clusteringStat.addValue(TopologyAnalyser.clusteringCoefficient(graph));
		}

		TopologyAnalysisMacroResult summary = new TopologyAnalysisMacroResult(
				degreeStat.getMean(), diameterStat.getMean(),
				clusteringStat.getMean());
		
		tac.print(System.out);
		summary.print(System.out);
		
		System.out.println();
	}
}
