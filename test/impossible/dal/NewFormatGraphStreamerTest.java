package impossible.dal;

import static org.junit.Assert.*;

import impossible.dal.NewFormatGraphStreamer;
import impossible.model.AdjacencyListFactory;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;


public class NewFormatGraphStreamerTest {

	private final static int NUM_NODES = 2;
	private final static int NUM_GRAPHS = 1;

	private BufferedReader getBillyGraphBufferedReader() {
		String string = "2\n" + "2\n" + "0.00 1.00\n" + "2.00 3.00\n" + "1\n"
				+ "0 1 100.0 200.0\n";

		InputStream inputStream = new ByteArrayInputStream(string.getBytes());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		return bufferedReader;
	}

	private Graph getBillyGraph(GraphFactory graphFactory) {
		List<Node> nodes = new ArrayList<>();
		nodes.add(new Node(0, 0.0, 1.0));
		nodes.add(new Node(1, 2.0, 3.0));

		List<Edge> edges = new ArrayList<>();
		edges.add(new Edge(0, 1, Arrays.asList(new Double[] { 100.0, 200.0 })));

		Graph graph = graphFactory.createFromLists(nodes, edges);

		return graph;
	}

	@Test
	public void testHasNext() {
		// Input.
		BufferedReader bufferedReader = getBillyGraphBufferedReader();

		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Initialize SUT.
		NewFormatGraphStreamer sut = new NewFormatGraphStreamer(NUM_NODES,
				NUM_GRAPHS, graphFactory, bufferedReader);

		// Exercise SUT.
		assertTrue(sut.hasNext());
		sut.getNext();
		assertFalse(sut.hasNext());
	}

	@Test
	public void testGetNext() {

		// Input.
		BufferedReader bufferedReader = getBillyGraphBufferedReader();

		// Helpers.
		GraphFactory graphFactory = new AdjacencyListFactory();

		// Initialize SUT.
		NewFormatGraphStreamer sut = new NewFormatGraphStreamer(NUM_NODES,
				NUM_GRAPHS, graphFactory, bufferedReader);

		// Exercise SUT.
		Graph actualGraph = sut.getNext();

		// Expected.
		Graph expectedGraph = getBillyGraph(graphFactory);

		// Assertions.
		assertEquals(expectedGraph, actualGraph);
	}

}
