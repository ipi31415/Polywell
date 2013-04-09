package utilities;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Immutable representation of a vector of doubles
 * @author Ryan Dewey
 */
public class DoubleVector implements Cloneable, Serializable{
	public static final double EPSILON = 1.0E-7;
	
	private final int size;
	private List<Double> values;
	
	/**
	 * Creates a new vector of the given size
	 */
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
	
	/**
	 * Creates a new vector with the given data
	 */
	public DoubleVector(double... values) {
		size = values.length;
		this.values = Lists.<Double>newArrayList();
		for (int i = 0; i < size; i++) {
			this.values.add(values[i]);
		}
	}
	
	/**
	 * Creates a new vector with the given data
	 */
	public DoubleVector(List<Double> values) {
		size = values.size();
		this.values = Lists.<Double>newArrayList();
		for (int i = 0; i < size; i++) {
			this.values.add(values.get(i));
		}
	}
	
	/**
	 * Gets a list of the data in this vector
	 */
	public List<Double> getValues() {
		return values;
	}
	
	/**
	 * Gets the value at a given index
	 */
	public Double getValue(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return values.get(index);
	}
	
	/**
	 * Sets the value at a given index
	 */
	public DoubleVector setValue(int index, Double value) {
		if (index >= size) {
			throw new IndexOutOfBoundsException();
		}
		DoubleVector result = clone();
		result.values.set(index, value);
		return result;
	}
	
	/**
	 * Gets the length of the vector
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Adds another vector
	 */
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
	
	/**
	 * Subtracts another vector
	 */
	public DoubleVector subtract(DoubleVector other) {
		return add(other.multiply(-1));
	}
	
	/**
	 * Multiplies by a scalar
	 */
	public DoubleVector multiply(double scalar) {
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, getValue(i) * scalar);
		}
		return result;
	}
	
	/**
	 * Divides by a scalar
	 */
	public DoubleVector divide(double scalar) {
		if (scalar == 0) {
			throw new ArithmeticException("Division by 0");
		}
		return multiply(1 / scalar);
	}
	
	/**
	 * Computes the dot product with another vector
	 */
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
	
	/**
	 * Computes the cross product with another vector
	 */
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
	
	/**
	 * Multiplies two vectors together element by element
	 */
	public DoubleVector elementMultiply(DoubleVector other) {
		if (size != other.size) {
			throw new IllegalArgumentException("Size of vectors must be equal");
		}
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, getValue(i) * other.getValue(i));
		}
		return result;
	}
	
	/**
	 * Divides two vectors element by element
	 */
	public DoubleVector elementDivide(DoubleVector other) {
		if (size != other.size) {
			throw new IllegalArgumentException("Size of vectors must be equal");
		}
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, getValue(i) / other.getValue(i));
		}
		return result;
	}
	
	/**
	 * Computes the norm of the vector
	 */
	public double norm() {
		return Math.sqrt(dotProduct(this));
	}
	
	/**
	 * Computes the normalized version of this vector
	 */
	public DoubleVector normalize() {
		if (this.norm() == 0) {
			return this;
		}
		return this.divide(norm());
	}
	
	/**
	 * Takes the absolute value of each element in this vector
	 */
	public DoubleVector abs() {
		DoubleVector result = new DoubleVector(size);
		for (int i = 0; i < size; i++) {
			result = result.setValue(i, Math.abs(getValue(i)));
		}
		return result;
	}
	
	/**
	 * Returns the maximum element of this vector
	 */
	public double max() {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			max = Math.max(max, getValue(i));
		}
		return max;
 	}
	
	/**
	 * Returns the minimum element of this vector
	 */
	public double min() {
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			min = Math.min(min, getValue(i));
		}
		return min;
	}
	
	/**
	 * Gets the projection of this vector onto a plane normal to the given vector
	 */
	public DoubleVector getProjection(DoubleVector dir) {
		DoubleVector n = dir.normalize();
		return subtract(n.multiply(dotProduct(n)));
	}
	
	/**
	 * Gets the two dimensional projection of this vector onto a plane normal to the given vector
	 * with the up direction as the given vector
	 */
	public DoubleVector getProjection3Dto2D(DoubleVector dir, DoubleVector up) {
		up = up.getProjection(dir);
		DoubleVector xPrime = getProjection(dir);
		DoubleVector right = dir.crossProduct(up).normalize();
		return new DoubleVector(xPrime.dotProduct(right), xPrime.dotProduct(up));
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
	
	/**
	 * Compares this vector two another with the given accuracy
	 */
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
