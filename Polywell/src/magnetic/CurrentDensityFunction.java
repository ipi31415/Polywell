package magnetic;

import utilities.DoubleVector;

import com.google.common.base.Function;

public abstract class CurrentDensityFunction implements Function<DoubleVector, DoubleVector> {
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