package magnetic;

import integration.Integrator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class MagneticField {
	public static final int DEFAULT_MONTE_CARLO_ITERATIONS = 100000;
	
	private final CurrentDensityFunction j;
	private final List<Pair<Double, Double>> ranges;

	private HashMap<DoubleVector, DoubleVector> results = Maps.<DoubleVector, DoubleVector>newHashMap();
	
	public MagneticField(CurrentDensityFunction j, List<Pair<Double, Double>> ranges) {
		this.j = j;
		this.ranges = ranges;
	}
	
	public void addResult(DoubleVector x, DoubleVector fx) {
		results.put(x, fx);
	}
	
	public void storeResults(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(results);

        oos.close();
	}
	
	@SuppressWarnings("unchecked")
	public void readResults(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		results = (HashMap<DoubleVector, DoubleVector>) ois.readObject();
		
		ois.close();
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
				DoubleVector direction = relative.normalize();
				return j.apply(x).crossProduct(direction).divide(Math.pow(relative.norm(), 2));
			}
		};
	}
}
