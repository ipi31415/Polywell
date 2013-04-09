package magnetic;

import java.io.IOException;
import java.util.List;

import utilities.DoubleVector;
import utilities.Pair;

/**
 * Precomputes a magnetic field and stores to file
 * @author Ryan Dewey
 */
public class MagneticFieldPrecomputer {

	public static void main(String[] args) {
		String filename = "Cubic Polywell30";
		double radius = 4;
		double thickness = 0.5;
		double magnitude = 1.0;
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> polywell = 
				CurrentDensityFunctionFactory.getCubicPolywellFunction(radius, thickness, magnitude);
		MagneticField field = new MagneticField(polywell.getA(), polywell.getB(), filename);
		//field.clear(); //Uncomment to remove old data
		
		int gridPoints = 30; //Number of points to compute in each dimension
		double rangeFactor = 2; //Factor to extend range past outside of polywell
		Pair<Double, Double> xRange = polywell.getB().get(0);
		Pair<Double, Double> yRange = polywell.getB().get(1);
		Pair<Double, Double> zRange = polywell.getB().get(2);
		DoubleVector gridWidths = new DoubleVector((xRange.getB() - xRange.getA()) * rangeFactor / gridPoints,
				(yRange.getB() - yRange.getA()) * rangeFactor/ gridPoints,
				(zRange.getB() - zRange.getA()) * rangeFactor/ gridPoints);
		
		for (double x = xRange.getA() * rangeFactor; x <= xRange.getB() * rangeFactor; 
				x += gridWidths.getValue(0)) {
			for (double y = yRange.getA() * rangeFactor; y <= yRange.getB() * rangeFactor; 
					y += gridWidths.getValue(1)) {
				for (double z = zRange.getA() * rangeFactor; z <= zRange.getB() * rangeFactor; 
						z += gridWidths.getValue(2)) {
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
