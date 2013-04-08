package test.magnetic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import magnetic.CurrentDensityFunction;
import magnetic.CurrentDensityFunctionFactory;
import magnetic.MagneticField;

import org.junit.Test;

import utilities.DoubleVector;
import utilities.Pair;

public class MagneticFieldTest {
	
	private Pair<CurrentDensityFunction, List<Pair<Double, Double>>> getTorusFlat0() {
		DoubleVector center = new DoubleVector(0, 0, 0);
		DoubleVector direction = new DoubleVector(0, 0, 1);
		double radius = 5.0;
		double thickness = 1.0;
		double magnitude = 2.0;
		return CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, center, direction, magnitude);
	}

	@Test
	public void fieldCenterTorusTest1() {
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> temp = getTorusFlat0();
		CurrentDensityFunction j = temp.getA();
		List<Pair<Double, Double>> ranges = temp.getB();
		MagneticField field = new MagneticField(j, ranges, "test");
		long time = System.currentTimeMillis();
		DoubleVector center = field.getField(new DoubleVector(0, 0, 0));
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(center);
	}
	
	@Test
	public void testReadWrite() {
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> temp = getTorusFlat0();
		CurrentDensityFunction j = temp.getA();
		List<Pair<Double, Double>> ranges = temp.getB();
		MagneticField field = new MagneticField(j, ranges, "test");
		field.addResult(new DoubleVector(1, 2, 3), new DoubleVector(1, 2, 3));
		field.addResult(new DoubleVector(1, 2, 4), new DoubleVector(6, 6, 6));
		try {
			field.storeResults();
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		}
		MagneticField readField = new MagneticField(j, ranges, "test");
		try {
			readField.readResults();
		} catch (IOException e) {
			fail("IOException");
		} catch (ClassNotFoundException e) {
			fail("ClassNotFoundException");
		}
		
		assertEquals(new DoubleVector(1, 2, 3), readField.getField(new DoubleVector(1, 2, 3)));
		assertEquals(new DoubleVector(6, 6, 6), readField.getField(new DoubleVector(1, 2, 4)));
	}
}
