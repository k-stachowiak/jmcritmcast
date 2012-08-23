package impossible.pfnd.mlarac;

import impossible.model.Path;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;

public class IntersectLambdaEstimator implements LambdaEstimator {

	@Override
	public List<Double> estimate(List<Double> constraints, Path exceedingPath,
			List<Path> nonExceedingPaths) {

		RealMatrix coefficients = computeCoefficients(constraints.size(),
				exceedingPath, nonExceedingPaths);

		RealVector constants = computeConstants(constraints.size(),
				exceedingPath, nonExceedingPaths);

		DecompositionSolver solver = new LUDecompositionImpl(coefficients)
				.getSolver();

		RealVector solution = solver.solve(constants);

		List<Double> result = new ArrayList<>();
		for (double c : solution.getData()) {
			result.add(c);
		}

		return result;
	}

	private RealVector computeConstants(int numConstraints, Path exceedingPath,
			List<Path> nonExceedingPaths) {

		double[] constants = new double[numConstraints];

		double metrZeroOfExPath = exceedingPath.getMetrics().get(0);
		for (int m = 0; m < numConstraints; ++m) {
			double metrZeroOfNonExPath = nonExceedingPaths.get(m).getMetrics()
					.get(0);

			constants[m] = metrZeroOfExPath - metrZeroOfNonExPath;
		}

		return new ArrayRealVector(constants, false);
	}

	private RealMatrix computeCoefficients(int numConstraints,
			Path exceedingPath, List<Path> nonExceedingPaths) {

		double[][] coefficients = new double[numConstraints][];

		for (int row = 0; row < numConstraints; ++row) {

			coefficients[row] = new double[numConstraints];
			for (int column = 0; column < numConstraints; ++column) {

				double metricOfExPath = exceedingPath.getMetrics().get(
						column + 1);

				double metricOfNonExPath = nonExceedingPaths.get(row)
						.getMetrics().get(column + 1);

				coefficients[row][column] = metricOfNonExPath - metricOfExPath;
			}
		}

		return new Array2DRowRealMatrix(coefficients, false);
	}
}