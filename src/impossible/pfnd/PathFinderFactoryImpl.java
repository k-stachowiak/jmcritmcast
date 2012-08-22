package impossible.pfnd;

import impossible.helpers.metrprov.IndexMetricProvider;
import impossible.helpers.metrprov.MetricProvider;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import impossible.pfnd.dkstr.DijkstraPathFinder;
import impossible.pfnd.dkstr.DijkstraRelaxation;
import impossible.pfnd.hmcp.HmcpPathFinder;

import java.util.List;


public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder CreateDijkstra(DijkstraRelaxation dijkstraRelaxation) {
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public PathFinder CreateDijkstraIndex(int metricIndex) {

		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);

		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);

		return new DijkstraPathFinder(dijkstraRelaxation);
	}

	@Override
	public PathFinder CreateHmcp(List<Double> constraints) {
		return new HmcpPathFinder(this, constraints);
	}
}
