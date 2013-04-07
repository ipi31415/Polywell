package test.integration;

import static org.junit.Assert.assertTrue;

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
			@Override
			public DoubleVector apply(DoubleVector x) {
				return new DoubleVector(x.dotProduct(x));
			}
		};
	}
	
	public Function<DoubleVector, DoubleVector> getSquareRootFunction() {
		return new Function<DoubleVector, DoubleVector>() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				return new DoubleVector(Math.sqrt(x.getValue(0)));
			}
		};
	}
	
	public Function<DoubleVector, DoubleVector> getVectorFunction() {
		return new Function<DoubleVector, DoubleVector>() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				return x.multiply(2.0);
			}
		};
	}

	@Test
	public void test1DSquareTrap() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		double accuracy = 1.0E-5;
		DoubleVector val = Integrator.integrateTrap(f, ranges, accuracy);
		System.out.println("Actual value: " + val + "\nExpected value: [2.666666...]\n");
		
		assertTrue(new DoubleVector(8.0/3.0).equals(val, accuracy));
	}
	
	@Test
	public void test1DSquareMC() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		double accuracy = 1.0E-2;
		DoubleVector val = Integrator.integrateMonteCarlo(f, ranges, 1000000);
		System.out.println("Actual value: " + val + "\nExpected value: [2.666666...]\n");
		
		assertTrue(new DoubleVector(8.0/3.0).equals(val, accuracy * 10));
	}
	
	@Test
	public void test1DSquareRootTrap() {
		Function<DoubleVector, DoubleVector> f = getSquareRootFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		double accuracy = 1.0E-5;
		DoubleVector val = Integrator.integrateTrap(f, ranges, accuracy);
		System.out.println("Actual value: " + val + "\nExpected value: [1.8856...]\n");
		
		assertTrue(new DoubleVector(4.0 * Math.sqrt(2) / 3).equals(val, accuracy));
	}

	@Test
	public void test2DSquareTrap() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		double accuracy = 1.0E-1;
		DoubleVector val = Integrator.integrateTrap(f, ranges, accuracy);
		System.out.println("Actual value: " + val + "\nExpected value: [10.666666...]\n");
		
		assertTrue(new DoubleVector(32.0/3.0).equals(val, accuracy));
	}
	
	@Test
	public void test2DSquareMC() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		double accuracy = 1.0E-2;
		DoubleVector val = Integrator.integrateMonteCarlo(f, ranges, 1000000);
		System.out.println("Actual value: " + val + "\nExpected value: [10.666666...]\n");
		
		assertTrue(new DoubleVector(32.0/3.0).equals(val, accuracy * 10));
	}
	
	@Test
	public void test3DSquareTrap() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		ranges.add(range);
		double accuracy = 1.0E-0;
		DoubleVector val = Integrator.integrateTrap(f, ranges, accuracy);
		System.out.println("Actual value: " + val + "\nExpected value: [32.0]\n");
		
		assertTrue(new DoubleVector(32.0).equals(val, accuracy));
	}
	
	@Test
	public void test3DSquareMC() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		ranges.add(range);
		double accuracy = 1.0E-2;
		DoubleVector val = Integrator.integrateMonteCarlo(f, ranges, 1000000);
		System.out.println("Actual value: " + val + "\nExpected value: [32.0]\n");
		
		assertTrue(new DoubleVector(32.0).equals(val, accuracy * 10));
	}
	
	@Test
	public void test10DSquareMC() {
		Function<DoubleVector, DoubleVector> f = getSquareFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		for (int i = 0; i < 10; i++) {
			ranges.add(range);
		}
		double accuracy = 1.0E-2;
		DoubleVector val = Integrator.integrateMonteCarlo(f, ranges, 1000000);
		System.out.println("Actual value: " + val + "\nExpected value: [13653.3333]\n");
		
		assertTrue(new DoubleVector(40960.0/3.0).equals(val, accuracy * 1000));
	}
	
	@Test
	public void testVectorTrap() {
		Function<DoubleVector, DoubleVector> f = getVectorFunction();
		Pair<Double, Double> range = Pair.<Double, Double>of(0.0,2.0);
		ArrayList<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(range);
		ranges.add(range);
		double accuracy = 1.0E-1;
		DoubleVector val = Integrator.integrateTrap(f, ranges, accuracy);
		System.out.println("Actual value: " + val + "\nExpected value: [8.0, 8.0]\n");
		
		assertTrue(new DoubleVector(8.0, 8.0).equals(val, accuracy));
	}
	
	@Test
	public void testRandomInRange() {
		DoubleVector min = new DoubleVector(-5.0, -5.0, -5.0, -5.0, -5.0);
		DoubleVector max = new DoubleVector(2.0, 2.0, 2.0, 2.0, 2.0);
		DoubleVector rand = Integrator.getRandomInRange(min, max);
		int dim = min.getSize();
		for (int i = 0; i < dim; i++) {
			assertTrue(rand.getValue(i) > min.getValue(i) && 
					rand.getValue(i) < max.getValue(i));
		}
	}
}
