package edu.ppt.impossible.pfnd;

import java.util.List;

import edu.ppt.impossible.pfnd.dkstr.DijkstraRelaxation;

public interface PathFinderFactory {

	PathFinder CreateDijkstra(DijkstraRelaxation dijkstraRelaxation);

	PathFinder CreateDijkstraIndex(int m);
	
	PathFinder CreateHmcp(List<Double> constraints);

}
