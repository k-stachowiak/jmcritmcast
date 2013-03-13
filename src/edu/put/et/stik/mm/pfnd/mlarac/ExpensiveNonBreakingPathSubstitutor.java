package edu.put.et.stik.mm.pfnd.mlarac;


import java.util.ArrayList;
import java.util.List;

import edu.put.et.stik.mm.model.topology.Path;

public class ExpensiveNonBreakingPathSubstitutor implements PathSubstiutor {

	@Override
	public List<Path> substitute(Path candidate, List<Path> nonExceedingPaths,
			List<Double> constraints) {
		
		List<Path> result = new ArrayList<>(nonExceedingPaths);

		int highestIndex = -1;
		double highestMetric = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < constraints.size(); ++i) {
			double metric = candidate.getMetrics().get(i + 1);
			if(metric <= constraints.get(i) && metric > highestMetric) {
				highestMetric = metric;
				highestIndex = i;
			}				
		}
		
		result.set(highestIndex, candidate);
		
		return result;
	}

}
