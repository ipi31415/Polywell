package test.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import integration.Integrator;
import utilities.DoubleVector;
import utilities.Pair;

public class IntegratorTest {
	
	public Function<DoubleVector, DoubleVector> getSquareFunction() {
		return new Function<DoubleVector, DoubleVector>() {
			public DoubleVector apply(DoubleVector x) {
				return new DoubleVector(x.dotProduct(x));
			}
		};
	}

	@Test
	public void test1DSquare() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = new Pair<Double, Double>(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		DoubleVector val = Integrator.integrate(f, ranges);
		
		assertEquals(new DoubleVector(8.0/3.0), val);
	}

	@Test
	public void test2DSquare() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = new Pair<Double, Double>(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		DoubleVector val = Integrator.integrate(f, ranges);
		assertEquals(new DoubleVector(32.0/3.0), val);
	}
}
