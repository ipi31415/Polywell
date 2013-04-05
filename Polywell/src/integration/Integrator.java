package integration;

import java.util.List;

import com.google.common.base.Function;

import utilities.DoubleVector;
import utilities.Pair;

public class Integrator {
	private static double EPSILON = 1.0E-7;
	
	public static DoubleVector integrate(Function<DoubleVector, DoubleVector> f, 
			List<Pair<Double, Double>> ranges) {
		int dim = ranges.size();
		double gridSteps = 10000;
		DoubleVector minCoord = new DoubleVector(dim);
		DoubleVector maxCoord = new DoubleVector(dim);
		DoubleVector steps = new DoubleVector(dim);
		for (int i = 0; i < dim; i++) {
			minCoord = minCoord.setValue(i, ranges.get(i).a);
			maxCoord = maxCoord.setValue(i, ranges.get(i).b);
			steps = steps.setValue(i, (ranges.get(i).b - ranges.get(i).a) / gridSteps);
		}
		DoubleVector total = new DoubleVector(f.apply(minCoord).getSize());
		DoubleVector coord = minCoord.multiply(1);
		while (coord.getValue(dim - 1) <= maxCoord.getValue(dim - 1)) {
			double mult = Math.pow(2, dim - numMinMax(coord, minCoord, maxCoord));
			DoubleVector value = f.apply(coord).multiply(mult);
			total = total.add(value);
			int index = 0;
			while (index < dim - 1 && coord.getValue(index) >= maxCoord.getValue(index)) {
				coord = coord.setValue(index, minCoord.getValue(index));
				index++;
			}
			coord = coord.setValue(index, coord.getValue(index) + steps.getValue(index));
		}
		double prodSteps = 1 / Math.pow(2, dim);
		for (int i = 0; i < dim; i++) {
			prodSteps *= steps.getValue(i);
		}
		return total.multiply(prodSteps);
	}
	
	private static int numMinMax(DoubleVector check, DoubleVector min, DoubleVector max) {
		int count = 0;
		int dim = check.getSize();
		if (min.getSize() != dim || max.getSize() != dim) {
			throw new IllegalArgumentException("Arrays must be same length");
		}
		for (int i = 0; i < dim; i++) {
			if (Math.abs(check.getValue(i) - min.getValue(i)) < EPSILON
					|| Math.abs(check.getValue(i) - max.getValue(i)) < EPSILON) {
				count++;
			}
		}
		return count;
	}
}