package integration;

import java.util.List;

import com.google.common.base.Function;

import utilities.DoubleVector;
import utilities.Pair;

/**
 * Provides methods for numerical integration
 * @author Ryan Dewey
 */
public class Integrator {
	private static double EPSILON = 1.0E-7;
	
	/**
	 * Arbitrary dimensional trapezoidal integration method
	 * @param f function to integrate
	 * @param ranges range to integrate over
	 * @param accuracy desired accuracy
	 * @return result of integration
	 */
	public static DoubleVector integrateTrap(Function<DoubleVector, DoubleVector> f, 
			List<Pair<Double, Double>> ranges, double accuracy) {
		int dim = ranges.size();
		double gridSteps = 10;
		DoubleVector minCoord = new DoubleVector(dim);
		DoubleVector maxCoord = new DoubleVector(dim);
		DoubleVector steps = new DoubleVector(dim);
		for (int i = 0; i < dim; i++) {
			minCoord = minCoord.setValue(i, ranges.get(i).getA());
			maxCoord = maxCoord.setValue(i, ranges.get(i).getB());
			steps = steps.setValue(i, (ranges.get(i).getB() - ranges.get(i).getA()) / gridSteps);
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
				steps = steps.setValue(i, (ranges.get(i).getB() - ranges.get(i).getA()) / gridSteps);
			}
			total = total.multiply(prodSteps);
		} while (prevTotal.subtract(total).abs().max() > accuracy);
		return total;
	}
	
	/**
	 * Arbitrary dimensional Monte-Carlo integration method
	 * @param f function to integrate
	 * @param ranges range to integrate over
	 * @param minRuns number of points to use in the Monte-Carlo integration
	 * @return result of integration
	 */
	public static DoubleVector integrateMonteCarlo(Function<DoubleVector, DoubleVector> f, 
			List<Pair<Double, Double>> ranges, int minRuns) {
		int dim = ranges.size();
		double gridSteps = 10;
		DoubleVector minCoord = new DoubleVector(dim);
		DoubleVector maxCoord = new DoubleVector(dim);
		DoubleVector steps = new DoubleVector(dim);
		for (int i = 0; i < dim; i++) {
			minCoord = minCoord.setValue(i, ranges.get(i).getA());
			maxCoord = maxCoord.setValue(i, ranges.get(i).getB());
			steps = steps.setValue(i, (ranges.get(i).getB() - ranges.get(i).getA()) / gridSteps);
		}
		double volume = getVolume(minCoord, maxCoord);
		DoubleVector total = new DoubleVector(f.apply(minCoord).getSize());
		int num = 0;
		do {
			num++;
			DoubleVector coord = getRandomInRange(minCoord, maxCoord);
			DoubleVector value = f.apply(coord);
			total = total.add(value);
		} while (num < minRuns);
		
		return total.multiply(volume).divide(num);
	}
	
	/**
	 * Returns number of elements on the border of the integration range for trapezoidal method
	 * @param check vector to check for border elements
	 * @param min minimum point of each axis
	 * @param max maximum point of each axis
	 * @return number of border elements
	 */
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
	
	/**
	 * Gets a random vector in the given range
	 * @param min minimum point of each axis
	 * @param max maximum point of each axis
	 * @return random vector with each element between min and max
	 */
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
	
	/**
	 * Gets the volume contained in the given range
	 * @param min minimum point of each axis
	 * @param max maximum point of each axis
	 * @return total volume
	 */
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