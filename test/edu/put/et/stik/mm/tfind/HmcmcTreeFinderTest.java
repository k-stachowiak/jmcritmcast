package edu.put.et.stik.mm.tfind;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.helpers.ConstraintsComparerImpl;
import edu.put.et.stik.mm.helpers.PathAggregator;
import edu.put.et.stik.mm.helpers.PathAggregatorImpl;
import edu.put.et.stik.mm.helpers.metrprov.IndexMetricProvider;
import edu.put.et.stik.mm.helpers.metrprov.MetricProvider;
import edu.put.et.stik.mm.model.topology.AdjacencyListFactory;
import edu.put.et.stik.mm.model.topology.EdgeDefinition;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.GraphFactory;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Tree;
import edu.put.et.stik.mm.pfnd.PathFinderFactory;
import edu.put.et.stik.mm.pfnd.PathFinderFactoryImpl;
import edu.put.et.stik.mm.tfind.ConstrainedSteinerTreeFinder;
import edu.put.et.stik.mm.tfind.SpanningTreeFinder;
import edu.put.et.stik.mm.tfind.TreeFinderFactory;
import edu.put.et.stik.mm.tfind.TreeFinderFactoryImpl;

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
		ConstrainedSteinerTreeFinder treeFinder = treeFinderFacotry
				.createHmcmc(constraintsComparer, pathFinderFactory,
						pathAggregator);

		Tree result = treeFinder.find(graph, spanned, constraints);

		// Assertions.
		assertNotNull(result);
	}

}
