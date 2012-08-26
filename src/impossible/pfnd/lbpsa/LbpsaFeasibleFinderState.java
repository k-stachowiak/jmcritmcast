package impossible.pfnd.lbpsa;

import impossible.model.Path;
import impossible.pfnd.dkstr.DefaultDijkstraRelaxation;

import java.util.List;

public class LbpsaFeasibleFinderState {

	private final DefaultDijkstraRelaxation relaxation;
	private final List<Double> lambdas;
	private final double upperBound;
	private final Path feasiblePath;

	public LbpsaFeasibleFinderState(DefaultDijkstraRelaxation relaxation,
			List<Double> lambdas, double upperBound, Path feasiblePath) {
		this.relaxation = relaxation;
		this.lambdas = lambdas;
		this.upperBound = upperBound;
		this.feasiblePath = feasiblePath;
	}

	public DefaultDijkstraRelaxation getRelaxation() {
		return relaxation;
	}

	public List<Double> getLambdas() {
		return lambdas;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public Path getFeasiblePath() {
		return feasiblePath;
	}
}
