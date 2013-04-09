package magnetic;

import integration.Integrator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * Describes a magnetic field
 * @author Ryan Dewey
 */
public class MagneticField {
	public static final int DEFAULT_MONTE_CARLO_ITERATIONS = 100000;
	
	private final CurrentDensityFunction j;
	private final List<Pair<Double, Double>> ranges;

	private String filename;
	
	private HashMap<DoubleVector, DoubleVector> results = Maps.<DoubleVector, DoubleVector>newHashMap();
	
	/**
	 * Creates a new magnetic field
	 * @param j current density to use
	 * @param ranges maximum extent of current
	 * @param filename file to load from and save to
	 */
	public MagneticField(CurrentDensityFunction j, List<Pair<Double, Double>> ranges, String filename) {
		this.j = j;
		this.ranges = ranges;
		this.filename = filename;
		try {
			readResults();
		} catch (Exception e) {}
	}
	
	/**
	 * Clears loaded data
	 */
	public void clear() {
		results.clear();
	}
	
	/**
	 * Returns all currently computed points
	 * @return map of all known points
	 */
	public HashMap<DoubleVector, DoubleVector> getResults() {
		return results;
	}
	
	/**
	 * Directly adds a vector to result map. TESTING ONLY
	 * @param x start point of vector
	 * @param fx vector
	 */
	public void addResult(DoubleVector x, DoubleVector fx) {
		results.put(x, fx);
	}
	
	/**
	 * Reads magnetic field from a file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readResults() throws IOException, ClassNotFoundException {
		Scanner in = new Scanner(new File(filename + ".field"));
		results = Maps.<DoubleVector, DoubleVector>newHashMap();
		while(in.hasNextLine()) {
			Scanner line = new Scanner(in.nextLine());
			double a = line.nextDouble();
			double b = line.nextDouble();
			double c = line.nextDouble();
			DoubleVector x = new DoubleVector(a, b, c);
			a = line.nextDouble();
			b = line.nextDouble();
			c = line.nextDouble();
			DoubleVector fx = new DoubleVector(a, b, c);
			results.put(x, fx);
		}
		in.close();
	}
	
	/**
	 * Stores results to a file
	 * @throws IOException
	 */
	public void storeResults() throws IOException {
		PrintWriter out = new PrintWriter(filename + ".field");
		for (DoubleVector v : results.keySet()) {
			out.print(v.getValue(0) + " " + v.getValue(1) + " " + v.getValue(2) + " ");
			DoubleVector res = results.get(v);
			out.println(res.getValue(0) + " " + res.getValue(1) + " " + res.getValue(2));
		}
		out.close();
	}
	
	/**
	 * Gets the current density used for computing the magnetic field
	 * @return current density function
	 */
	public CurrentDensityFunction getCurrentDensityFunction() {
		return j;
	}
	
	/**
	 * Gets field from results without computing if not present
	 * @param coord point to find field at
	 * @return field at given point or null if not computed
	 */
	public DoubleVector getFieldLoaded(DoubleVector coord) {
		return results.get(coord);
	}
	
	/**
	 * Gets the field at a given point. Computes using a Monte-Carlo integration if not known
	 * @param coord point to find field at
	 * @return field at given point
	 */
	public DoubleVector getField(DoubleVector coord) {
		if (results.get(coord) != null) {
			return results.get(coord);
		}
		DoubleVector result = Integrator.integrateMonteCarlo(getBiotSavartFunction(coord), ranges, 
				DEFAULT_MONTE_CARLO_ITERATIONS);
		results.put(coord, result);
		return result;
	}
	
	/**
	 * Gets the function to integrate over from the Biot-Savart law
	 * @param coord point to find field at
	 * @return function describing integrand for this point.
	 */
	public Function<DoubleVector, DoubleVector> getBiotSavartFunction(final DoubleVector coord) {
		return new Function<DoubleVector, DoubleVector>() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				DoubleVector relative = coord.subtract(x);
				if (relative.norm() == 0) {
					return new DoubleVector(0, 0, 0);
				}
				DoubleVector direction = relative.normalize();
				return j.apply(x).crossProduct(direction).divide(Math.pow(relative.norm(), 2));
			}
		};
	}
}
