package magnetic;

import java.io.IOException;
import java.util.List;

import utilities.DoubleVector;
import utilities.Pair;

public class MagneticFieldPrecomputer {

	public static void main(String[] args) {
		String filename = "Cubic Polywell30";
		double radius = 4;
		double thickness = 0.5;
		double magnitude = 1.0;
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> polywell = 
				CurrentDensityFunctionFactory.getCubicPolywellFunction(radius, thickness, magnitude);
		MagneticField field = new MagneticField(polywell.getA(), polywell.getB(), filename);
		//field.clear();
		
		int gridPoints = 30;
		Pair<Double, Double> xRange = polywell.getB().get(0);
		Pair<Double, Double> yRange = polywell.getB().get(1);
		Pair<Double, Double> zRange = polywell.getB().get(2);
		DoubleVector gridWidths = new DoubleVector((xRange.getB() - xRange.getA()) * 2 / gridPoints,
				(yRange.getB() - yRange.getA()) * 2/ gridPoints,
				(zRange.getB() - zRange.getA()) * 2/ gridPoints);
		
		for (double x = xRange.getA() * 2; x <= xRange.getB() * 2; x += gridWidths.getValue(0)) {
			for (double y = yRange.getA() * 2; y <= yRange.getB() * 2; y += gridWidths.getValue(1)) {
				for (double z = zRange.getA() * 2; z <= zRange.getB() * 2; z += gridWidths.getValue(2)) {
					DoubleVector coord = new DoubleVector(x, y, z);
					System.out.println(coord);
					field.getField(coord);
					try {
						field.storeResults();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
