package edu.ppt.impossible.pfnd;

import edu.ppt.impossible.helpers.DefaultDijkstraRelaxation;
import edu.ppt.impossible.helpers.DijkstraRelaxation;
import edu.ppt.impossible.helpers.IndexMetricProvider;
import edu.ppt.impossible.helpers.MetricProvider;

public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder CreateDijkstraIndex(int metricIndex) {
		
		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);
		
		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);
		
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

}
