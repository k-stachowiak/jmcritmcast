package edu.ppt.impossible.pfnd;

import edu.ppt.impossible.helpers.metrprov.IndexMetricProvider;
import edu.ppt.impossible.helpers.metrprov.MetricProvider;
import edu.ppt.impossible.pfnd.dkstr.DefaultDijkstraRelaxation;
import edu.ppt.impossible.pfnd.dkstr.DijkstraPathFinder;
import edu.ppt.impossible.pfnd.dkstr.DijkstraRelaxation;

public class PathFinderFactoryImpl implements PathFinderFactory {

	@Override
	public PathFinder CreateDijkstraIndex(int metricIndex) {
		
		MetricProvider metricProvider = new IndexMetricProvider(metricIndex);
		
		DijkstraRelaxation dijkstraRelaxation = new DefaultDijkstraRelaxation(
				metricProvider);
		
		return new DijkstraPathFinder(dijkstraRelaxation);
	}

}
