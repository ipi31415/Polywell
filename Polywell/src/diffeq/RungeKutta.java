package diffeq;

import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.base.Function;

/**
 * Methods for obtaining a runge-kutta approximation for solving an ODE
 * @author Ryan Dewey
 */
public class RungeKutta {
	
	/**
	 * Computes a 4th order runge kutta approximation of the next state given the current state, time,
	 * timestep, and derivatives function.
	 * @param x current state vector
	 * @param t current time
	 * @param tau current timestep
	 * @param derivs derivative function
	 * @return next state vector
	 */
	public static DoubleVector rungeKutta4(DoubleVector x, double t, double tau, 
			Function<Pair<DoubleVector, Double>, DoubleVector> derivs) {
		double halfTau = 0.5 * tau;
		DoubleVector f1 = derivs.apply(Pair.of(x, t));
		double tHalf = t + halfTau;
		DoubleVector xtemp = x.add(f1.multiply(halfTau));
		DoubleVector f2 = derivs.apply(Pair.of(xtemp, tHalf));
		xtemp = x.add(f2.multiply(halfTau));
		DoubleVector f3 = derivs.apply(Pair.of(xtemp, tHalf));
		double tFull = t + tau;
		xtemp = x.add(f3.multiply(tau));
		DoubleVector f4 = derivs.apply(Pair.of(xtemp, tFull));
		DoubleVector xOut = f1.add(f4.add(f2.add(f3).multiply(2))).multiply(tau / 6).add(x);
		return xOut;
	}
}
