package utilities;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class DoubleVector implements Cloneable, Serializable{
	public static final double EPSILON = 1.0E-7;
	
	private final int size;
	private List<Double> values;
	
	public DoubleVector(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size of vector must be positive.");
		}
		this.size = size;
		values = Lists.<Double>newArrayList();
		for (int i = 0; i < size; i++) {
			values.add(0.0);
		}
	}
	
	public DoubleVector(double... values) {
		size = values.length;
		this.values = Lists.<Double>newArrayList();
		for (int i = 0; i < size; i++) {
			this.values.add(values[i]);
		}
	}
	
	public DoubleVector(List<Double> values) {
		size = values.size();
		this.values = Lists.<Double>newArrayList();
		for (int i = 0; i < size; i++) {
			this.values.add(values.get(i));
		}
	}
	
	public List<Double> getValues() {
		return values;
	}
	
	public Double getValue(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return values.get(index);
	}
	
	public DoubleVector setValue(int index, Double value) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
		DoubleVector result = clone();
		result.values.set(index, value);
		return result;
	}
	
	public int getSize() {
		return size;
	}
	
	public DoubleVector add(DoubleVector other) {
		if (size != other.size) {
			throw new IllegalArgumentException("Size of vectors must be equal");
		}
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, getValue(i) + other.getValue(i));
		}
		return result;
	}
	
	public DoubleVector subtract(DoubleVector other) {
		return add(other.multiply(-1));
	}
	
	public DoubleVector multiply(double scalar) {
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, getValue(i) * scalar);
		}
		return result;
	}
	
	public DoubleVector divide(double scalar) {
		if (scalar == 0) {
			throw new ArithmeticException("Division by 0");
		}
		return multiply(1 / scalar);
	}
	
	public double dotProduct(DoubleVector other) {
		if (size != other.size) {
			throw new IllegalArgumentException("Size of vectors must be equal");
		}
		double result = 0;
		for (int i = 0; i < size; i++) {
			result += getValue(i) * other.getValue(i);
		}
		return result;
	}
	
	public DoubleVector crossProduct(DoubleVector other) {
		if (size != other.size || size != 3) {
			throw new IllegalArgumentException("Size of vectors must be 3");
		}
		DoubleVector result = new DoubleVector(size);
		result = result.setValue(0, getValue(1) * other.getValue(2) - getValue(2) * other.getValue(1));
		result = result.setValue(1, getValue(2) * other.getValue(0) - getValue(0) * other.getValue(2));
		result = result.setValue(2, getValue(0) * other.getValue(1) - getValue(1) * other.getValue(0));
		return result;
	}
	
	public double norm() {
		return Math.sqrt(dotProduct(this));
	}
	
	public DoubleVector normalize() {
		return this.divide(norm());
	}
	
	public DoubleVector abs() {
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, Math.abs(getValue(i)));
		}
		return result;
	}
	
	public double max() {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			max = Math.max(max, getValue(i));
		}
		return max;
 	}
	
	public double min() {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			min = Math.min(min, getValue(i));
		}
		return min;
	}
	
	@Override
	public DoubleVector clone() {
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result.values.set(i, getValue(i));
		}
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		return equals((DoubleVector) other, EPSILON);
	}
	
	public boolean equals(DoubleVector other, double accuracy) {
		for (int i = 0; i < size; i++) {
			if (Math.abs(getValue(i) - other.getValue(i)) >= accuracy) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(values);
	}
	
	@Override
	public String toString() {
		return values.toString();
	}
}
