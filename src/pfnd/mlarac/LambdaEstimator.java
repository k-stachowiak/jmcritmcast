package pfnd.mlarac;


import java.util.List;

import model.topology.Path;


public interface LambdaEstimator {

	List<Double> estimate(List<Double> constraints, Path exceedingPath,
			List<Path> nonExceedingPaths);

}
