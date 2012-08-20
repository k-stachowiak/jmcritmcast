package edu.ppt.impossible.pfnd;

import java.util.List;

import edu.ppt.impossible.helpers.metrprov.IndexMetricProvider;
import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import edu.ppt.impossible.pfnd.dkstr.DijkstraPathFinder;
import edu.ppt.impossible.pfnd.dkstr.DijkstraRelaxation;
import edu.ppt.impossible.pfnd.hmcp.HmcpPathFinder;

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
