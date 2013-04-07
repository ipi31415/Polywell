package magnetic;

import integration.Integrator;

import java.util.List;

import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.base.Function;

public class MagneticField {
	public static final int DEFAULT_MONTE_CARLO_ITERATIONS = 100000;
	
	private final CurrentDensityFunction j;
	private final List<Pair<Double, Double>> ranges;

	public MagneticField(CurrentDensityFunction j, List<Pair<Double, Double>> ranges) {
		this.j = j;
		this.ranges = ranges;
	}
	
	public DoubleVector getField(DoubleVector coord) {
		return Integrator.integrateMonteCarlo(getBiotSavartFunction(coord), ranges, 
				DEFAULT_MONTE_CARLO_ITERATIONS);
	}
	
	public Function<DoubleVector, DoubleVector> getBiotSavartFunction(final DoubleVector coord) {
		return new Function<DoubleVector, DoubleVector>() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				DoubleVector relative = coord.subtract(x);
				DoubleVector direction = relative.normalize();
				return j.apply(x).crossProduct(direction).divide(Math.pow(relative.norm(), 2));
			}
		};
	}
}
