package dal;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;

import dto.EdgeDTO;
import dto.GraphDTO;
import dto.NodeDTO;

public class InetGraphReader implements GraphReader {

	@Override
	public GraphDTO readGraph(Scanner scanner) {

		ArrayList<NodeDTO> nodes = new ArrayList<>();
		ArrayList<EdgeDTO> edges = new ArrayList<>();

		scanner.findWithinHorizon("(\\d+) (\\d+)", 0);
		MatchResult headerMatch = scanner.match();
		int nodesCount = Integer.parseInt(headerMatch.group(1));
		int edgesCount = Integer.parseInt(headerMatch.group(2));

		for (int i = 0; i < nodesCount; ++i) {
			scanner.findWithinHorizon("(\\d+)\\s+(\\d+)\\s+(\\d+)", 0);
			MatchResult nodeMatch = scanner.match();
			int id = Integer.parseInt(nodeMatch.group(1));
			int x = Integer.parseInt(nodeMatch.group(2));
			int y = Integer.parseInt(nodeMatch.group(3));
			nodes.add(new NodeDTO(id, x, y));
		}

		for (int i = 0; i < edgesCount; ++i) {
			scanner.findWithinHorizon("(\\d+)\\s+(\\d+)\\s+(\\d+)", 0);
			MatchResult edgeMatch = scanner.match();
			int from = Integer.parseInt(edgeMatch.group(1));
			int to = Integer.parseInt(edgeMatch.group(2));
			ArrayList<Double> metrics = new ArrayList<>();
			metrics.add(Double.parseDouble(edgeMatch.group(3)));
			edges.add(new EdgeDTO(from, to, metrics));
		}

		return new GraphDTO(nodes, edges);
	}

}
