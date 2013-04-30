package edu.put.et.stik.mm.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import dal.NewFormatGraphStreamer;
import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;


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

	private GraphDTO getBillyGraph() {
		List<NodeDTO> nodes = new ArrayList<>();
		nodes.add(new NodeDTO(0, 0.0, 1.0));
		nodes.add(new NodeDTO(1, 2.0, 3.0));

		List<EdgeDTO> edges = new ArrayList<>();
		edges.add(new EdgeDTO(0, 1, Arrays.asList(new Double[] { 100.0, 200.0 })));

		return new GraphDTO(nodes, edges);
	}

	@BeforeClass
	public static void beforeClass() {
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void testHasNext() {
		// Input.
		BufferedReader bufferedReader = getBillyGraphBufferedReader();

		// Initialize SUT.
		NewFormatGraphStreamer sut = new NewFormatGraphStreamer(NUM_NODES,
				NUM_GRAPHS, bufferedReader);

		// Exercise SUT.
		assertTrue(sut.hasNext());
		sut.getNext();
		assertFalse(sut.hasNext());
	}

	@Test
	public void testGetNext() {

		// Input.
		BufferedReader bufferedReader = getBillyGraphBufferedReader();

		// Initialize SUT.
		NewFormatGraphStreamer sut = new NewFormatGraphStreamer(NUM_NODES,
				NUM_GRAPHS, bufferedReader);

		// Exercise SUT.
		GraphDTO actualGraph = sut.getNext();

		// Expected.
		GraphDTO expectedGraph = getBillyGraph();

		// Assertions.
		assertEquals(expectedGraph, actualGraph);
	}

}
