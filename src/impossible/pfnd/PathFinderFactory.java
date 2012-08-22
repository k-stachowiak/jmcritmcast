package impossible.pfnd;

import impossible.pfnd.dkstr.DijkstraRelaxation;

import java.util.List;


public interface PathFinderFactory {

	PathFinder CreateDijkstra(DijkstraRelaxation dijkstraRelaxation);

	PathFinder CreateDijkstraIndex(int m);
	
	PathFinder CreateHmcp(List<Double> constraints);

}
