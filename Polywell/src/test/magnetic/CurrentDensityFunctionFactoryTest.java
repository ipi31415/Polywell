package test.magnetic;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.base.Function;

import magnetic.CurrentDensityFunctionFactory;
import utilities.DoubleVector;

public class CurrentDensityFunctionFactoryTest {
	
	@Test
	public void testTorusFunction() {
		Function<DoubleVector, DoubleVector> f = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(5.0, 1.0, 
						new DoubleVector(0.0, 0.0, 0.0), new DoubleVector(0.0, 0.0, 1.0), 
						5.0).getA();
		DoubleVector x = new DoubleVector(3.0, 3.0, 0);
		for (int i = 0; i < 1000; i++) {
			f.apply(x);
		}
		//System.out.println(f.apply(new DoubleVector(3.0, 3.0, 0)));
		assertTrue(f.apply(new DoubleVector(0.0, 5.0, 0.0)).norm() > 0);
		assertTrue(f.apply(new DoubleVector(0.0, 3.9, 0.0)).norm() == 0);
	}
	
	@Test
	public void testTorusFunction2() {
		Function<DoubleVector, DoubleVector> f = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(3.7, 2.0, 
						new DoubleVector(1.0, 2.0, 3.0), new DoubleVector(1.0, 1.0, 1.0), 
						5.0).getA();
		//System.out.println(f.apply(new DoubleVector(1 + 3.7/Math.sqrt(3) + .1, 2 + 3.7/Math.sqrt(3) - .1, 
		//		3 - 3.7/Math.sqrt(3))));
		assertTrue(f.apply(new DoubleVector(1 + 3.7/Math.sqrt(3) + .1, 2 + 3.7/Math.sqrt(3) - .1, 
				3 - 3.7/Math.sqrt(3))).norm() > 0);
		assertTrue(f.apply(new DoubleVector(0.0, 5.0, 0.0)).norm() == 0);
	}
}
