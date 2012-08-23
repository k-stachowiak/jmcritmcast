package impossible.pfnd.mlarac;

import impossible.model.Path;

import java.util.ArrayList;
import java.util.List;

public class MeanPairsLambdaEstimator implements LambdaEstimator {

	@Override
	public List<Double> estimate(List<Double> constraints, Path exceedingPath,
			List<Path> nonExceedingPaths) {

		List<Double> result = new ArrayList<>();
		for (int p = 0; p < nonExceedingPaths.size(); ++p) {
			double lhs = nonExceedingPaths.get(p).getMetrics().get(p + 1); // TODO: use offset here.
			double rhs = exceedingPath.getMetrics().get(p + 1);
			result.add((lhs + rhs) * 0.5);
		}

		return result;
	}

}
