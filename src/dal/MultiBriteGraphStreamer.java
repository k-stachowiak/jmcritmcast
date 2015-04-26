package dal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import apps.topanal.data.TopologyType;
import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;

public class MultiBriteGraphStreamer implements InputGraphStreamer {

	private final int graphsLimit;
	private final List<File> graphsFiles;
	private int lastIndex;

	public MultiBriteGraphStreamer(String path, TopologyType type,
			int nodesCount, int graphsLimit) {

		this.graphsLimit = graphsLimit;
		this.graphsFiles = getMatchingFiles(path, type, nodesCount);
		this.lastIndex = 0;

		if (this.graphsLimit > this.graphsFiles.size()) {
			throw new RuntimeException(
					"Incorrect count limit put on the given resource.");
		}
	}

	@Override
	public boolean hasNext() {
		return lastIndex < graphsLimit;
	}

	@Override
	public GraphDTO getNext() {

		File file = graphsFiles.get(lastIndex++);
		try {
			Scanner scanner = new Scanner(file);
			GraphDTO result = readGraph(scanner);
			return result;

		} catch (FileNotFoundException e) {
			System.err.printf("Failed reading file %s", file.getPath());
			return null;

		}
	}

	static GraphDTO readGraph(Scanner scanner) {

		ArrayList<NodeDTO> nodes = new ArrayList<>();
		ArrayList<EdgeDTO> edges = new ArrayList<>();

		// Match header to retrieve the primitives' counts.
		scanner.findWithinHorizon("Topology: \\( (\\d+) Nodes, (\\d+) Edges \\)", 0);
		MatchResult headerMatch = scanner.match();
		int nodesCount = Integer.parseInt(headerMatch.group(1));
		int edgesCount = Integer.parseInt(headerMatch.group(2));

		for (int i = 0; i < nodesCount; ++i) {
			scanner.findWithinHorizon("(\\d+)	(\\d+)	(\\d+)	\\d+	\\d+	\\d+	AS_NODE", 0);
			MatchResult nodeMatch = scanner.match();
			int id = Integer.parseInt(nodeMatch.group(1));
			int x = Integer.parseInt(nodeMatch.group(2));
			int y = Integer.parseInt(nodeMatch.group(3));
			nodes.add(new NodeDTO(id, x, y));
		}
		
		for (int i = 0; i < edgesCount; ++i) {
			scanner.findWithinHorizon("\\d+	(\\d+)	(\\d+)	(\\d+\\.\\d+)	(\\d+\\.\\d+)	10.0	\\d+	\\d+	E_AS	U", 0);
			MatchResult edgeMatch = scanner.match();
			int from = Integer.parseInt(edgeMatch.group(1));
			int to = Integer.parseInt(edgeMatch.group(2));
			ArrayList<Double> metrics = new ArrayList<>();
			metrics.add(Double.parseDouble(edgeMatch.group(3)));
			metrics.add(Double.parseDouble(edgeMatch.group(4)));
			edges.add(new EdgeDTO(from, to, metrics));
		}

		return new GraphDTO(nodes, edges);
	}

	private static List<File> getMatchingFiles(String path, TopologyType type,
			int nodesCount) {

		String topString = type.name();
		String countString = Integer.toString(nodesCount);

		ArrayList<File> result = new ArrayList<>();

		for (File f : new File(path).listFiles()) {
			if (f.isDirectory()) {
				continue;
			}
			String fileName = f.getName();
			if (fileName.startsWith(topString + "." + countString + ".")) {
				result.add(f);
			}
		}

		return result;
	}

}
