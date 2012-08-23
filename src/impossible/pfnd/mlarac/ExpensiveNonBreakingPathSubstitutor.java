package impossible.pfnd.mlarac;

import impossible.model.Path;

import java.util.ArrayList;
import java.util.List;

public class ExpensiveNonBreakingPathSubstitutor implements PathSubstiutor {

	@Override
	public List<Path> substitute(Path candidate, List<Path> nonExceedingPaths,
			List<Double> constraints) {
		
		List<Path> result = new ArrayList<>(nonExceedingPaths);

		int highestIndex = -1;
		double highestMetric = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < constraints.size(); ++i) {
			double metric = candidate.getMetrics().get(i + 1); // TODO: Use offset variable here!
			if(metric <= constraints.get(i) && metric > highestMetric) {
				highestMetric = metric;
				highestIndex = i;
			}				
		}
		
		result.set(highestIndex, candidate);
		
		return result;
	}

}
