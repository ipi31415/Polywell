package magnetic;

import com.google.common.base.Function;

import utilities.DoubleMatrix;
import utilities.DoubleVector;

public class CurrentDensityFunctionFactory {
	public static Function<DoubleVector, DoubleVector> getTorusDensityFunction(final double radius,
			final double thickness, final DoubleVector center, final DoubleVector direction,
			final double magnitude) {
		final DoubleVector z = new DoubleVector(0, 0, 1);
		DoubleVector crossRotation = direction.crossProduct(z);
		double dotRotation = direction.dotProduct(z);
		double norm = dotRotation * dotRotation + crossRotation.dotProduct(crossRotation);
		crossRotation = crossRotation.divide(norm);
		dotRotation /= norm;
		final DoubleMatrix matrix = new DoubleMatrix(3,3);
		matrix.setValue(0, 0,  1 - 2 * crossRotation.getValue(1) - 2 * crossRotation.getValue(2));
		matrix.setValue(0, 1, 2 * crossRotation.getValue(0) * crossRotation.getValue(1) - 
				2 * crossRotation.getValue(2) * dotRotation);
		matrix.setValue(0, 2, 2 * crossRotation.getValue(0) * crossRotation.getValue(2) + 
				2 * crossRotation.getValue(1) * dotRotation);
		matrix.setValue(1, 0, 2 * crossRotation.getValue(0) * crossRotation.getValue(1) + 
				2 * crossRotation.getValue(2) * dotRotation);
		matrix.setValue(1, 1,  1 - 2 * crossRotation.getValue(0) - 2 * crossRotation.getValue(2));
		matrix.setValue(1, 2, 2 * crossRotation.getValue(1) * crossRotation.getValue(2) - 
				2 * crossRotation.getValue(0) * dotRotation);
		matrix.setValue(2, 0, 2 * crossRotation.getValue(0) * crossRotation.getValue(2) - 
				2 * crossRotation.getValue(1) * dotRotation);
		matrix.setValue(2, 1, 2 * crossRotation.getValue(1) * crossRotation.getValue(2) + 
				2 * crossRotation.getValue(0) * dotRotation);
		matrix.setValue(2, 2,  1 - 2 * crossRotation.getValue(0) - 2 * crossRotation.getValue(1));
		
		return new Function<DoubleVector, DoubleVector>() {
			public DoubleVector apply(DoubleVector x) {
				DoubleVector centerDistance = x.subtract(center);
				DoubleVector xTrans = matrix.multiply(centerDistance);
				double ringDistance = Math.sqrt(Math.pow(xTrans.getValue(2), 2) + 
						Math.pow(Math.sqrt(Math.pow(xTrans.getValue(0), 2) + 
								Math.pow(xTrans.getValue(1), 2)) - radius, 2));
				if (ringDistance <= thickness) {
					return direction.crossProduct(x).normalize().multiply(magnitude);
				} else {
					return new DoubleVector(0, 0, 0);
				}
			}
		};
	}
}
