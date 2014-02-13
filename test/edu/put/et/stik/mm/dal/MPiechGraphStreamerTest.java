package edu.put.et.stik.mm.dal;

import static org.junit.Assert.*;

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

import dal.MPiechGraphStreamer;
import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;

public class MPiechGraphStreamerTest {

	private static final int NUM_GRAPHS = 2;

	private BufferedReader getJoeysEdgeBufferedReader() {
		
		String string =
			"1\n" +
			"0	1	100	200\n" +
			"1	2	200 300\n" +
			"2	0	300	400\n" +
			"2\n" +
			"0	1	400	500\n" +
			"1	2	500	600\n" +
			"2	0	600	700";
		
		InputStream inputStream = new ByteArrayInputStream(string.getBytes());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		return bufferedReader;
	}

	private BufferedReader getJoeysNodeBufferedReader() {
		String string =
			"1\n" +
			"0	10	20\n" +
			"1	30	40\n" +
			"2	50	60\n" +
			"2\n" +
			"0	70	80\n" +
			"1	90	100\n" +
			"2	110	120";
		
		InputStream inputStream = new ByteArrayInputStream(string.getBytes());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		return bufferedReader;
	}

	private GraphDTO getJoey1Graph() {
		List<NodeDTO> nodes = new ArrayList<>();
		nodes.add(new NodeDTO(0, 10.0, 20));
		nodes.add(new NodeDTO(1, 30.0, 40));
		nodes.add(new NodeDTO(2, 50.0, 60));
		
		List<EdgeDTO> edges = new ArrayList<>();
		edges.add(new EdgeDTO(0, 1, Arrays.asList(new Double[] { 100.0, 200.0 })));
		edges.add(new EdgeDTO(1, 2, Arrays.asList(new Double[] { 200.0, 300.0 })));
		edges.add(new EdgeDTO(2, 0, Arrays.asList(new Double[] { 300.0, 400.0 })));
		
		return new GraphDTO(nodes, edges);
	}
	
	private GraphDTO getJoey2Graph() {
		List<NodeDTO> nodes = new ArrayList<>();
		nodes.add(new NodeDTO(0, 70.0, 80.0));
		nodes.add(new NodeDTO(1, 90.0, 100.0));
		nodes.add(new NodeDTO(2, 110.0, 120.0));
		
		List<EdgeDTO> edges = new ArrayList<>();
		edges.add(new EdgeDTO(0, 1, Arrays.asList(new Double[] { 400.0, 500.0 })));
		edges.add(new EdgeDTO(1, 2, Arrays.asList(new Double[] { 500.0, 600.0 })));
		edges.add(new EdgeDTO(2, 0, Arrays.asList(new Double[] { 600.0, 700.0 })));
		
		return new GraphDTO(nodes, edges);
	}

	@BeforeClass
	public static void beforeClass() {
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void testHasNext() {
		// Input.
		BufferedReader nodeReader = getJoeysNodeBufferedReader();
		BufferedReader edgeReader = getJoeysEdgeBufferedReader();

		// Initialize SUT.
		MPiechGraphStreamer sut = new MPiechGraphStreamer(NUM_GRAPHS, nodeReader, edgeReader);
		
		// Exercise SUT.
		assertTrue(sut.hasNext());
		sut.getNext();
		assertTrue(sut.hasNext());
		sut.getNext();
		assertFalse(sut.hasNext());
	}

	@Test
	public void testGetNext() {
		// Input.
		BufferedReader nodeReader = getJoeysNodeBufferedReader();
		BufferedReader edgeReader = getJoeysEdgeBufferedReader();

		// Initialize SUT.
		MPiechGraphStreamer sut = new MPiechGraphStreamer(NUM_GRAPHS, nodeReader, edgeReader);
		
		// Exercise SUT.
		GraphDTO actualGraph1 = sut.getNext();
		GraphDTO actualGraph2 = sut.getNext();
		
		// Expected.
		GraphDTO expectedGraph1 = getJoey1Graph();
		GraphDTO expectedGraph2 = getJoey2Graph();
		
		// Assertions.
		assertEquals(expectedGraph1, actualGraph1);
		assertEquals(expectedGraph2, actualGraph2);
	}

}
