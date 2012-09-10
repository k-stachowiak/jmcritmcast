package impossible.dal;

import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.GraphFactory;
import impossible.model.Node;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class NewFormatGraphStreamer implements InputGraphStreamer {

    private final int numNodes;
    private final int numGraphs;
    private final GraphFactory graphFactory;
    private final Scanner scanner;
    private int graphsRead;

    public NewFormatGraphStreamer(int numNodes, int numGraphs,
            GraphFactory graphFactory, BufferedReader bufferedReader) {

        this.numNodes = numNodes;
        this.numGraphs = numGraphs;
        this.graphFactory = graphFactory;
        scanner = new Scanner(bufferedReader);

        graphsRead = 0;
    }

    @Override
    public boolean hasNext() {
        return graphsRead < numGraphs;
    }

    @Override
    public Graph getNext() {
        int currentNumNodes = scanner.nextInt();
        int currentNumMetrics = scanner.nextInt();

        if (currentNumNodes != numNodes) {
            throw new IllegalArgumentException();
        }

        int currentId = 0;
        List<Node> nodes = new ArrayList<>();
        for (int n = 0; n < currentNumNodes; ++n) {
            double x, y;

            try {
                x = scanner.nextDouble();
                y = scanner.nextDouble();
            } catch (InputMismatchException ex) {
                String badToken = scanner.next();
                System.err.printf("Token \"%1$s\" encountered while double expected.", badToken);
                return null;
            }

            nodes.add(new Node(currentId, x, y));
            ++currentId;
        }

        int currentNumEdges = scanner.nextInt();
        List<Edge> edges = new ArrayList<>();
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
                    System.err.printf("Token \"%1$s\" encountered while double expected.", badToken);
                    return null;
                }

                metrics.add(metric);
            }

            edges.add(new Edge(nodeFrom, nodeTo, metrics));
        }

        Graph result = graphFactory.createFromLists(nodes, edges);

        ++graphsRead;
        return result;
    }
}
