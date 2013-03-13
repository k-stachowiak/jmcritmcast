package edu.put.et.stik.mm.pfnd.mlarac;


import java.util.List;

import edu.put.et.stik.mm.model.topology.Path;

public interface LambdaEstimator {

	List<Double> estimate(List<Double> constraints, Path exceedingPath,
			List<Path> nonExceedingPaths);

}
