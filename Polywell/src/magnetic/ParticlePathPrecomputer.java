package magnetic;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import diffeq.RungeKutta;

public class ParticlePathPrecomputer {
	
	public static void main(String[] args) {
		String filename = "Cubic Polywell30";
		double radius = 4;
		double thickness = 0.5;
		double magnitude = 1.0;
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> polywell = 
				CurrentDensityFunctionFactory.getCubicPolywellFunction(radius, thickness, magnitude);
		final MagneticField field = new MagneticField(polywell.getA(), polywell.getB(), filename);
	
		Function<Pair<DoubleVector, Double>, DoubleVector> deriv = 
				new Function<Pair<DoubleVector, Double>, DoubleVector>() {
			@Override
			public DoubleVector apply(Pair<DoubleVector, Double> xt) {
				DoubleVector xv = xt.getA();
				DoubleVector x = new DoubleVector(xv.getValue(0), xv.getValue(1),
						xv.getValue(2));
				DoubleVector v = new DoubleVector(xv.getValue(3), xv.getValue(4),
						xv.getValue(5));
				DoubleVector b = field.getField(x);
				//System.out.println(b);
				DoubleVector force = b.crossProduct(v).multiply(10);
				return new DoubleVector(v.getValue(0), v.getValue(1), v.getValue(2), 
						force.getValue(0), force.getValue(1), force.getValue(2));
			}
		};
		
		DoubleVector state = new DoubleVector(0, 0, 0, 1, 2, 3);

		List<DoubleVector> path = Lists.<DoubleVector>newArrayList();
		try {
			Scanner in = new Scanner(new File("tenth.path"));
			boolean check = false;
			while (in.hasNextLine()) {
				Scanner line = new Scanner(in.nextLine());
				DoubleVector next = new DoubleVector(line.nextDouble(), line.nextDouble(), line.nextDouble(), 
						line.nextDouble(), line.nextDouble(), line.nextDouble());
				System.out.println(next);
				if (!check) {
					check = next.equals(state);
				}
				if (!check) {
					break;
				}
				path.add(next);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int numSteps = 100;
		double timeStep = .1;
		if (path.size() == 0) {
			path.add(state);
		} else {
			state = path.get(path.size() - 1);
		}
		for (int i = path.size(); i < numSteps; i++) {
			state = RungeKutta.rungeKutta4(state, timeStep * i, timeStep, deriv);
			path.add(state);
			System.out.println(state);
		}
		try {
			PrintWriter out = new PrintWriter("tenth.path");
			for (DoubleVector p : path) {
				out.println(p.getValue(0) + " " + 
						p.getValue(1) + " " +
						p.getValue(2) + " " +
						p.getValue(3) + " " +
						p.getValue(4) + " " +
						p.getValue(5));
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
