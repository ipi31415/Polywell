package test.magnetic;

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
		MagneticField field = new MagneticField(j, ranges);
		long time = System.currentTimeMillis();
		DoubleVector center = field.getField(new DoubleVector(0, 0, 0));
		System.out.println(System.currentTimeMillis() - time);
		System.out.println(center);
	}
}
