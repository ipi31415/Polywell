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

public class MagneticField {
	public static final int DEFAULT_MONTE_CARLO_ITERATIONS = 100000;
	
	private final CurrentDensityFunction j;
	private final List<Pair<Double, Double>> ranges;

	private String filename;
	
	private HashMap<DoubleVector, DoubleVector> results = Maps.<DoubleVector, DoubleVector>newHashMap();
	
	public MagneticField(CurrentDensityFunction j, List<Pair<Double, Double>> ranges, String filename) {
		this.j = j;
		this.ranges = ranges;
		this.filename = filename;
		try {
			readResults();
		} catch (Exception e) {}
	}
	
	public void clear() {
		results.clear();
	}
	
	public HashMap<DoubleVector, DoubleVector> getResults() {
		return results;
	}
	
	public void addResult(DoubleVector x, DoubleVector fx) {
		results.put(x, fx);
	}
	
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
	
	public void storeResults() throws IOException {
		PrintWriter out = new PrintWriter(filename + ".field");
		for (DoubleVector v : results.keySet()) {
			out.print(v.getValue(0) + " " + v.getValue(1) + " " + v.getValue(2) + " ");
			DoubleVector res = results.get(v);
			out.println(res.getValue(0) + " " + res.getValue(1) + " " + res.getValue(2));
		}
		out.close();
	}
	
	public CurrentDensityFunction getCurrentDensityFunction() {
		return j;
	}
	
	public DoubleVector getFieldLoaded(DoubleVector coord) {
		return results.get(coord);
	}
	
	public DoubleVector getField(DoubleVector coord) {
		if (results.get(coord) != null) {
			return results.get(coord);
		}
		DoubleVector result = Integrator.integrateMonteCarlo(getBiotSavartFunction(coord), ranges, 
				DEFAULT_MONTE_CARLO_ITERATIONS);
		results.put(coord, result);
		return result;
	}
	
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
