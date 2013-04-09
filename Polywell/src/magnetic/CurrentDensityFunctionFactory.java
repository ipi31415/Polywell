package magnetic;

import java.util.List;

import utilities.DoubleMatrix;
import utilities.DoubleVector;
import utilities.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CurrentDensityFunctionFactory {
	public static Pair<CurrentDensityFunction, List<Pair<Double, Double>>> 
			getTorusDensityFunction(final double radius, final double thickness, final DoubleVector center, 
					final DoubleVector direction, final double magnitude) {
		
		List<Pair<Double, Double>> ranges = Lists.<Pair<Double, Double>>newArrayList();
		ranges.add(Pair.<Double, Double>of(center.getValue(0) - radius - thickness,
				center.getValue(0) + radius + thickness));
		ranges.add(Pair.<Double, Double>of(center.getValue(1) - radius - thickness,
				center.getValue(1) + radius + thickness));
		ranges.add(Pair.<Double, Double>of(center.getValue(2) - radius - thickness,
				center.getValue(2) + radius + thickness));
		
		final DoubleMatrix matrix = DoubleMatrix.getRotationMatrix(direction.normalize(), 
				new DoubleVector(0, 0, 1));
		
		CurrentDensityFunction f = new CurrentDensityFunction() {
			@Override
			public DoubleVector apply(DoubleVector x) {
				DoubleVector centerDistance = x.subtract(center);
				DoubleVector xTrans = matrix.multiply(centerDistance);
				double ringDistance = Math.sqrt(Math.pow(xTrans.getValue(2), 2) + 
						Math.pow(Math.sqrt(Math.pow(xTrans.getValue(0), 2) + 
								Math.pow(xTrans.getValue(1), 2)) - radius, 2));
				if (ringDistance <= thickness) {
					DoubleVector res = direction.crossProduct(x).normalize().multiply(magnitude);
					return res;
				} else {
					DoubleVector res = new DoubleVector(0, 0, 0);
					return res;
				}
			}
		};
		
		return Pair.<CurrentDensityFunction, List<Pair<Double, Double>>>of(f, 
				ImmutableList.<Pair<Double, Double>>copyOf(ranges));
	}

	
	public static Pair<CurrentDensityFunction, List<Pair<Double, Double>>> getCubicPolywellFunction(
			double radius, double thickness, double magnitude) {
		double centerDist = radius + Math.sqrt(2) * thickness;
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus1 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(centerDist, 0, 0), new DoubleVector(-1, 0, 0), magnitude);
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus2 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(-centerDist, 0, 0), new DoubleVector(1, 0, 0), magnitude);
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus3 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(0, centerDist, 0), new DoubleVector(0, -1, 0), magnitude);
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus4 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(0, -centerDist, 0), new DoubleVector(0, 1, 0), magnitude);
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus5 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(0, 0, centerDist), new DoubleVector(0, 0, -1), magnitude);
		Pair<CurrentDensityFunction, List<Pair<Double, Double>>> torus6 = 
				CurrentDensityFunctionFactory.getTorusDensityFunction(radius, thickness, 
						new DoubleVector(0, 0, -centerDist), new DoubleVector(0, 0, 1), magnitude);
		CurrentDensityFunction combined = CurrentDensityFunction.addFunctions(
				torus1.getA(), torus2.getA(), torus3.getA(), torus4.getA(), torus5.getA(), torus6.getA());
//		@SuppressWarnings("unchecked")
//		List<Pair<Double, Double>> totalRange = combineRanges(Lists.<List<Pair<Double, Double>>>newArrayList(
//				torus1.getB(), torus2.getB(), torus3.getB(), torus4.getB(), torus5.getB(), torus6.getB()));
		List<Pair<Double, Double>> totalRange = Lists.<Pair<Double, Double>>newArrayList();
		totalRange.add(Pair.of(-1 * centerDist - thickness, centerDist + thickness));
		totalRange.add(Pair.of(-1 * centerDist - thickness, centerDist + thickness));
		totalRange.add(Pair.of(-1 * centerDist - thickness, centerDist + thickness));
		return Pair.of(combined, totalRange);
	}
	
	public static List<Pair<Double, Double>> combineRanges(List<List<Pair<Double, Double>>> rangeLists) {
		List<Pair<Double, Double>> result = Lists.<Pair<Double, Double>>newArrayList();
		for (int i = 0; i < rangeLists.get(0).size(); i++) {
			DoubleVector mins = new DoubleVector(rangeLists.size());
			DoubleVector maxes = new DoubleVector(rangeLists.size());
			for (int j = 0; j < rangeLists.size(); j++) {
				mins = mins.setValue(j, rangeLists.get(j).get(i).getA());
				maxes = maxes.setValue(j, rangeLists.get(j).get(i).getB());
			}
			result.add(Pair.of(mins.min(), maxes.max()));
		}
		return result;
	}
}