package impossible.tfind;

import static org.junit.Assert.assertNotNull;
import impossible.helpers.ConstraintsComparer;
import impossible.helpers.ConstraintsComparerImpl;
import impossible.helpers.PathAggregator;
import impossible.helpers.PathAggregatorImpl;
import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.AdjacencyListFactory;
import impossible.model.EdgeDefinition;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;
import impossible.model.Tree;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.PathFinderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class HmcmcTreeFinderTest {

	@Test
	public final void testFind() {

		// Helpers.
		MetricProvider metricProvider = new IndexMetricProvider(0);
		ConstraintsComparer constraintsComparer = new ConstraintsComparerImpl();
		TreeFinderFactory treeFinderFacotry = new TreeFinderFactoryImpl();
		SpanningTreeFinder spanningTreeFinder = treeFinderFacotry
				.createPrim(metricProvider);
		PathFinderFactory pathFinderFactory = new PathFinderFactoryImpl();
		PathAggregator pathAggregator = new PathAggregatorImpl(
				spanningTreeFinder);

		// Model.
		List<EdgeDefinition> cheapEdgeDefinitions = new ArrayList<>();
		cheapEdgeDefinitions.add(new EdgeDefinition(0, 3));
		cheapEdgeDefinitions.add(new EdgeDefinition(3, 4));
		cheapEdgeDefinitions.add(new EdgeDefinition(1, 4));
		cheapEdgeDefinitions.add(new EdgeDefinition(3, 5));
		cheapEdgeDefinitions.add(new EdgeDefinition(5, 2));

		GraphFactory graphFactory = new AdjacencyListFactory();
		Graph graph = graphFactory.createDoubleTriangle(cheapEdgeDefinitions);

		List<Node> spanned = new ArrayList<>();
		spanned.add(graph.getNode(0));
		spanned.add(graph.getNode(1));
		spanned.add(graph.getNode(2));

		List<Double> constraints = new ArrayList<>();
		constraints.add(20.0);
		constraints.add(20.0);

		// Exercise SUT.
		ConstrainedSteinerTreeFinder treeFinder = treeFinderFacotry.createHmcmc(
				constraintsComparer, pathFinderFactory, pathAggregator);

		Tree result = treeFinder.find(graph, spanned, constraints);

		// Assertions.
		assertNotNull(result);
	}

}
