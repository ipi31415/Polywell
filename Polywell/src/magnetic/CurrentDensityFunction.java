package magnetic;

import utilities.DoubleVector;

import com.google.common.base.Function;

/**
 * Wrapper for Function to ensure that only valid current densities are used for magnetic field calculation.
 * @author Ryan Dewey
 */
public abstract class CurrentDensityFunction implements Function<DoubleVector, DoubleVector> {
	/**
	 * Returns a new function which adds the given functions
	 * @param functions functions to add
	 * @return single function which adds the given functions
	 */
	public static CurrentDensityFunction addFunctions(final CurrentDensityFunction... functions) {
		return new CurrentDensityFunction() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				DoubleVector result = new DoubleVector(functions[0].apply(x).getSize());
				for (CurrentDensityFunction f : functions) {
					result = result.add(f.apply(x));
				}
				return result;
			}
		};
	}
}