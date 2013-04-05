package integration;

import java.util.List;

import com.google.common.base.Function;

import utilities.DoubleVector;
import utilities.Pair;

public class Integrator {
	private static double EPSILON = 1.0E-7;
	
	public static DoubleVector integrateTrap(Function<DoubleVector, DoubleVector> f, 
			List<Pair<Double, Double>> ranges, double accuracy) {
		int dim = ranges.size();
		double gridSteps = 10;
		DoubleVector minCoord = new DoubleVector(dim);
		DoubleVector maxCoord = new DoubleVector(dim);
		DoubleVector steps = new DoubleVector(dim);
		for (int i = 0; i < dim; i++) {
			minCoord = minCoord.setValue(i, ranges.get(i).a);
			maxCoord = maxCoord.setValue(i, ranges.get(i).b);
			steps = steps.setValue(i, (ranges.get(i).b - ranges.get(i).a) / gridSteps);
		}
		DoubleVector prevTotal;
		DoubleVector total = new DoubleVector(f.apply(minCoord).getSize());
		do {
			prevTotal = total.multiply(1);
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
			gridSteps *= 2;
			steps = new DoubleVector(dim);
			for (int i = 0; i < dim; i++) {
				steps = steps.setValue(i, (ranges.get(i).b - ranges.get(i).a) / gridSteps);
			}
			total = total.multiply(prodSteps);
		} while (prevTotal.subtract(total).abs().max() > accuracy);
		return total;
	}
	
	public static DoubleVector integrateMonteCarlo(Function<DoubleVector, DoubleVector> f, 
			List<Pair<Double, Double>> ranges, int minRuns) {
		int dim = ranges.size();
		double gridSteps = 10;
		DoubleVector minCoord = new DoubleVector(dim);
		DoubleVector maxCoord = new DoubleVector(dim);
		DoubleVector steps = new DoubleVector(dim);
		for (int i = 0; i < dim; i++) {
			minCoord = minCoord.setValue(i, ranges.get(i).a);
			maxCoord = maxCoord.setValue(i, ranges.get(i).b);
			steps = steps.setValue(i, (ranges.get(i).b - ranges.get(i).a) / gridSteps);
		}
		double volume = getVolume(minCoord, maxCoord);
		DoubleVector total = new DoubleVector(f.apply(minCoord).getSize());
		//DoubleVector prevTotal = null;
		int num = 0;
		do {
			//prevTotal = total.multiply(1);
			num++;
			DoubleVector coord = getRandomInRange(minCoord, maxCoord);
			DoubleVector value = f.apply(coord);
			total = total.add(value);
		} while (num < minRuns);// || prevTotal.multiply(volume).divide(num - 1).subtract(
				//total.multiply(volume).divide(num)).abs().max() > accuracy);
		
		return total.multiply(volume).divide(num);
	}
	
	public static int numMinMax(DoubleVector check, DoubleVector min, DoubleVector max) {
		int dim = check.getSize();
		if (min.getSize() != dim || max.getSize() != dim) {
			throw new IllegalArgumentException("Arrays must be same length");
		}
		int count = 0;
		for (int i = 0; i < dim; i++) {
			if (Math.abs(check.getValue(i) - min.getValue(i)) < EPSILON
					|| Math.abs(check.getValue(i) - max.getValue(i)) < EPSILON) {
				count++;
			}
		}
		return count;
	}
	
	public static DoubleVector getRandomInRange(DoubleVector min, DoubleVector max) {
		int dim = min.getSize();
		if (dim != max.getSize()) {
			throw new IllegalArgumentException("Arrays must be same length");
		}
		DoubleVector result = new DoubleVector(min.getSize());
		for (int i = 0; i < dim; i++) {
			result = result.setValue(i, Math.random() * (max.getValue(i) - min.getValue(i)) + 
					min.getValue(i));
		}
		return result;
	}
	
	public static double getVolume(DoubleVector min, DoubleVector max) {
		int dim = min.getSize();
		if (dim != max.getSize()) {
			throw new IllegalArgumentException("Arrays must be same length");
		}
		double result = 1.0;
		for (int i = 0; i < dim; i++) {
			result *= max.getValue(i) - min.getValue(i);
		}
		return result;
	}
}