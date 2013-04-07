package magnetic;

import java.util.List;

import utilities.DoubleMatrix;
import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CurrentDensityFunctionFactory {
	public static Pair<CurrentDensityFunction, List<Pair<Double, Double>>> 
			getTorusDensityFunction(final double radius, final double thickness, final DoubleVector center, 
					final DoubleVector direction, final double magnitude) {
		final DoubleVector z = new DoubleVector(0, 0, 1);
		DoubleVector crossRotation = direction.crossProduct(z);
		double dotRotation = direction.dotProduct(z);
		double norm = dotRotation * dotRotation + crossRotation.dotProduct(crossRotation);
		crossRotation = crossRotation.divide(norm);
		dotRotation /= norm;
		DoubleMatrix tempMat = new DoubleMatrix(3,3);
		tempMat = tempMat.setValue(0, 0,  1 - 2 * crossRotation.getValue(1) - 2 * crossRotation.getValue(2));
		tempMat = tempMat.setValue(0, 1, 2 * crossRotation.getValue(0) * crossRotation.getValue(1) - 
				2 * crossRotation.getValue(2) * dotRotation);
		tempMat = tempMat.setValue(0, 2, 2 * crossRotation.getValue(0) * crossRotation.getValue(2) + 
				2 * crossRotation.getValue(1) * dotRotation);
		tempMat = tempMat.setValue(1, 0, 2 * crossRotation.getValue(0) * crossRotation.getValue(1) + 
				2 * crossRotation.getValue(2) * dotRotation);
		tempMat = tempMat.setValue(1, 1,  1 - 2 * crossRotation.getValue(0) - 2 * crossRotation.getValue(2));
		tempMat = tempMat.setValue(1, 2, 2 * crossRotation.getValue(1) * crossRotation.getValue(2) - 
				2 * crossRotation.getValue(0) * dotRotation);
		tempMat = tempMat.setValue(2, 0, 2 * crossRotation.getValue(0) * crossRotation.getValue(2) - 
				2 * crossRotation.getValue(1) * dotRotation);
		tempMat = tempMat.setValue(2, 1, 2 * crossRotation.getValue(1) * crossRotation.getValue(2) + 
				2 * crossRotation.getValue(0) * dotRotation);
		tempMat = tempMat.setValue(2, 2,  1 - 2 * crossRotation.getValue(0) - 2 * crossRotation.getValue(1));
		
		List<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(Pair.<Double, Double>of(center.getValue(0) - radius - thickness,
				center.getValue(0) + radius + thickness));
		ranges.add(Pair.<Double, Double>of(center.getValue(1) - radius - thickness,
				center.getValue(1) + radius + thickness));
		ranges.add(Pair.<Double, Double>of(center.getValue(2) - radius - thickness,
				center.getValue(2) + radius + thickness));
		
		final DoubleMatrix matrix = tempMat.clone();
		
		CurrentDensityFunction f = new CurrentDensityFunction() {
			@Override
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
		
		return Pair.<CurrentDensityFunction, List<Pair<Double, Double>>>of(f, 
				ImmutableList.<Pair<Double, Double>>copyOf(ranges));
	}
}