package impossible.pfnd.mlarac;

import impossible.model.Path;

import java.util.List;

public interface LambdaEstimator {

	List<Double> estimate(List<Double> constraints, Path exceedingPath,
			List<Path> nonExceedingPaths);

}