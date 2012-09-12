package impossible.pfnd.lbpsa;

import impossible.helpers.ConstraintsComparer;
import impossible.helpers.metrprov.LagrangeMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.model.Edge;
import impossible.model.Graph;
import impossible.model.Node;
import impossible.model.Path;
import impossible.pfnd.ConstrainedPathFinder;
import impossible.pfnd.PathFinder;
import impossible.pfnd.PathFinderFactory;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LbpsaFeasibleFinder implements ConstrainedPathFinder {

    // Strategies
    private final PathFinderFactory pathFinderFactory;
    private final ConstraintsComparer constraintsComparer;
    private LbpsaFeasibleFinderState feasibleState;

    public LbpsaFeasibleFinder(PathFinderFactory pathFinderFactory,
            ConstraintsComparer constraintsComparer) {

        // Strategies.
        this.pathFinderFactory = pathFinderFactory;
        this.constraintsComparer = constraintsComparer;
    }

    @Override
    public Path find(Graph graph, Node from, Node to, List<Double> constraints) {

        feasibleState = null;
        List<Double> lambdas = new ArrayList<>();
        for (int m = 0; m < graph.getNumMetrics() - 1; ++m) {
            lambdas.add(0.0);
        }

        double upperBound = initUb(graph);

        iterationLoop(graph, from, to, upperBound, lambdas, constraints);

        if (feasibleState == null) {
            return null;
        }

        return feasibleState.getFeasiblePath();
    }

    public List<Double> getLambdas() {

        if (feasibleState == null) {
            return null;
        }

        return feasibleState.getLambdas();
    }

    public Double getUpperBound() {

        if (feasibleState == null) {
            return null;
        }

        return feasibleState.getUpperBound();
    }

    public Map<Node, List<Double>> getSpecifficMetrics() {

        if (feasibleState == null) {
            return null;
        }

        return feasibleState.getRelaxation().getLabels();
    }

    private void iterationLoop(Graph graph, Node from, Node to,
            double currentUpperBound, List<Double> currentLambdas, List<Double> constraints) {

        double lk = 2.0;
        double prevL = Double.NaN;
        int iterationsSinceLChange = 0;
        int passes = 0;

        List<Double> result = new ArrayList<>(currentLambdas);

        while (true) {

            // Initialize finder.
            // ------------------
            MetricProvider metricProvider = new LagrangeMetricProvider(
                    1, constraints, result);

            DefaultDijkstraRelaxation relaxation = new DefaultDijkstraRelaxation(
                    metricProvider);

            PathFinder pathFinder = pathFinderFactory
                    .createDijkstra(relaxation);

            // Find and evaluate path.
            // -----------------------
            Path path = pathFinder.find(graph, from, to);

            if (path != null
                    && constraintsComparer.fulfilsAll(path, constraints)) {

                currentUpperBound = path.getMetrics().get(0);
                feasibleState = new LbpsaFeasibleFinderState(relaxation,
                        result, currentUpperBound, path);
                break;
            }

            // Iteration step.
            // ---------------
            double L = metricProvider.getPreAdditive(path);
            int metric = getMaxIndex(path, constraints);            
            double step = computeStep(path, metric, lk, currentUpperBound, L, constraints);
            
            result.set(metric - 1, result.get(metric - 1) + step);
            if (result.get(metric - 1) < 0.0) {
                result.set(metric - 1, 0.0);
            }

            // Purple magic.
            // -------------
            if (L == prevL) {
                ++iterationsSinceLChange;
            } else {
                iterationsSinceLChange = 0;
                prevL = L;
            }

            if (iterationsSinceLChange >= 10) {
                iterationsSinceLChange = 0;
                lk *= 0.5;
            }

            if (++passes > 100) {
                passes = 0;
                lk *= 0.5;
            }

            if (lk < 0.125) {
                break;
            }
        }
    }

    private Double initUb(Graph graph) {
        double sum = 0.0;
        for (Edge edge : graph.getEdges()) {
            sum += edge.getMetrics().get(0);
        }
        return sum;
    }

    private int getMaxIndex(Path path, List<Double> constraints) {

        int result = -1;
        double max = Double.NEGATIVE_INFINITY;

        for (int m = 1; m < path.getMetrics().size(); ++m) {
            double value = path.getMetrics().get(m) - constraints.get(m - 1);
            if (value > max) {
                max = value;
                result = m;
            }
        }

        return result;
    }

    private double computeStep(Path path, int metric, double lk,
            Double currentUpperBound, double L, List<Double> constraints) {

        double m = path.getMetrics().get(metric);
        double c = constraints.get(metric - 1);
        double denom = (m - c);
        
        if(denom == 0.0) {            
            return currentUpperBound * 0.01; // WAT
        }
        
        return (lk * (currentUpperBound - L)) / (denom * denom);
    }
}
