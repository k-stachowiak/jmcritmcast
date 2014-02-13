package dal;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;


public class NewFormatGraphStreamer implements InputGraphStreamer {

	private final int numNodes;
	private final int numGraphs;
	private final Scanner scanner;
	private int graphsRead;

	public NewFormatGraphStreamer(int numNodes, int numGraphs,
			BufferedReader bufferedReader) {

		this.numNodes = numNodes;
		this.numGraphs = numGraphs;
		scanner = new Scanner(bufferedReader);

		graphsRead = 0;
	}

	@Override
	public final boolean hasNext() {
		return graphsRead < numGraphs;
	}

	@Override
	public GraphDTO getNext() {
		
		int currentNumNodes = scanner.nextInt();
		int currentNumMetrics = scanner.nextInt();

		if (currentNumNodes != numNodes) {
			throw new IllegalArgumentException();
		}

		int currentId = 0;
		List<NodeDTO> nodes = new ArrayList<>();
		for (int n = 0; n < currentNumNodes; ++n) {
			double x, y;

			try {
				x = scanner.nextDouble();
				y = scanner.nextDouble();
			} catch (InputMismatchException ex) {
				String badToken = scanner.next();
				System.err.printf(
						"Token \"%1$s\" encountered while double expected.",
						badToken);
				return null;
			}

			nodes.add(new NodeDTO(currentId, x, y));
			++currentId;
		}

		int currentNumEdges = scanner.nextInt();
		List<EdgeDTO> edges = new ArrayList<>();
		for (int e = 0; e < currentNumEdges; ++e) {

			int nodeFrom = scanner.nextInt();
			int nodeTo = scanner.nextInt();

			List<Double> metrics = new ArrayList<>();
			for (int m = 0; m < currentNumMetrics; ++m) {

				double metric;

				try {
					metric = scanner.nextDouble();
				} catch (InputMismatchException ex) {
					String badToken = scanner.next();
					System.err
							.printf("Token \"%1$s\" encountered while double expected.",
									badToken);
					return null;
				}

				metrics.add(metric);
			}

			edges.add(new EdgeDTO(nodeFrom, nodeTo, metrics));
		}

		GraphDTO result = new GraphDTO(nodes, edges);

		++graphsRead;
		return result;
	}
}
