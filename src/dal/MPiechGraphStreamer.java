package dal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;

public class MPiechGraphStreamer implements InputGraphStreamer {

	private final int numGraphs;
	private final BufferedReader nodeReader;
	private final BufferedReader edgeReader;
	private int graphsRead;

	public MPiechGraphStreamer(int numGraphs, BufferedReader nodeReader,
			BufferedReader edgeReader) {

		this.numGraphs = numGraphs;
		this.nodeReader = nodeReader;
		this.edgeReader = edgeReader;

		graphsRead = 0;

		// Skip id line.
		try {
			nodeReader.readLine();
			edgeReader.readLine();
		} catch (IOException e) {
			throw new IllegalArgumentException("Incorrect buffer readers.");
		}
	}

	@Override
	public boolean hasNext() {
		return graphsRead < numGraphs;
	}

	@Override
	public GraphDTO getNext() {

		String nodeLine;
		List<NodeDTO> nodes = new ArrayList<>();
		try {
			for (;;) {
				nodeLine = nodeReader.readLine();
				if (nodeLine == null || isIdLine(nodeLine)) {
					break;
				}
				nodes.add(nodeFromLine(nodeLine));
			}
		} catch (IOException e) {
			return null;
		}

		String edgeLine;
		List<EdgeDTO> edges = new ArrayList<>();
		try {
			for (;;) {
				edgeLine = edgeReader.readLine();
				if (edgeLine == null || isIdLine(edgeLine)) {
					break;
				}
				edges.add(edgeFromLine(edgeLine));
			}
		} catch (IOException e) {
			return null;
		}

		++graphsRead;
		return new GraphDTO(nodes, edges);
	}

	private NodeDTO nodeFromLine(String nodeLine) {

		String[] pieces = nodeLine.split("\\s+");

		if (pieces.length != 3) {
			throw new IllegalArgumentException("Incorrect node line format.");
		}

		try {
			return new NodeDTO(Integer.parseInt(pieces[0]),
					Double.parseDouble(pieces[1]),
					Double.parseDouble(pieces[2]));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Incorrect node line format.");
		}
	}

	private EdgeDTO edgeFromLine(String edgeLine) {

		String[] pieces = edgeLine.split("\\s+");

		if (pieces.length < 3) {
			throw new IllegalArgumentException("Incorrect edge line format.");
		}

		List<Double> metrics = new ArrayList<>();

		try {
			for (int i = 2; i < pieces.length; ++i) {
				metrics.add(Double.parseDouble(pieces[i]));
			}

			return new EdgeDTO(Integer.parseInt(pieces[0]),
					Integer.parseInt(pieces[1]), metrics);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Incorrect edge line format.");
		}
	}

	private boolean isIdLine(String line) {
		try {
			Integer.parseInt(line);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
