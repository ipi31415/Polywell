package utilities;

import java.io.Serializable;

/**
 * Represents an arbitrarily sized immutable matrix of doubles.
 * @author Ryan Dewey
 */
public class DoubleMatrix implements Cloneable, Serializable {
	public static final double EPSILON = 1.0E-7;
	
	private int rows;
	private int columns;
	private double[][] matrix;
	
	/**
	 * Creates an empty matrix with the given number of rows and columns.
	 * @param rows number of rows
	 * @param columns number of columns
	 */
	public DoubleMatrix(int rows, int columns) {
		if (rows <= 0 || columns <= 0) {
			throw new IllegalArgumentException("Dimensions must be positive");
		}
		this.rows = rows;
		this.columns = columns;
		matrix = new double[rows][columns];
	}
	
	/**
	 * Creates a matrix from the given rows
	 * @param rows rows to create matrix from
	 */
	public DoubleMatrix(DoubleVector... rows) {
		this.rows = rows.length;
		this.columns = rows[0].getSize();
		matrix = new double[this.rows][columns];
		for (int r = 0; r < this.rows; r++) {
			if (rows[r].getSize() != columns) {
				throw new IllegalArgumentException("All rows must be the same length");
			}
			for (int c = 0; c < columns; c++) {
				matrix[r][c] = rows[r].getValue(c);
			}
		}
	}
	
	/**
	 * Gets the value at a given coordinate in the matrix
	 * @param row row value
	 * @param column column value
	 * @return value at given point
	 */
	public double getValue(int row, int column) {
		return matrix[row][column];
	}
	
	/**
	 * Sets the value at a given coordinate in the matrix
	 */
	public DoubleMatrix setValue(int row, int column, double val) {
		DoubleMatrix result = clone();
		result.matrix[row][column] = val;
		return result;
	}
	
	/**
	 * Gets a row from the matrix.
	 */
	public DoubleVector getRow(int row) {
		DoubleVector result = new DoubleVector(columns);
		for (int c = 0; c < columns; c++) {
			result = result.setValue(c, matrix[row][c]);
		}
		return result;
	}
	
	/**
	 * Gets a column from the matrix.
	 */
	public DoubleVector getColumn(int column) {
		DoubleVector result = new DoubleVector(rows);
		for (int r = 0; r < rows; r++) {
			result = result.setValue(r, matrix[r][column]);
		}
		return result;
	}
	
	/**
	 * Adds another matrix
	 */
	public DoubleMatrix add(DoubleMatrix other) {
		if (rows != other.rows || columns != other.columns) {
			throw new IllegalArgumentException("Matrices must be same size");
		}
		DoubleMatrix result = new DoubleMatrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				result = result.setValue(r, c, getValue(r, c) + other.getValue(r, c));
			}
		}
		return result;
	}
	
	/**
	 * Subtracts another matrix
	 */
	public DoubleMatrix subtract(DoubleMatrix other) {
		return add(other.multiply(-1));
	}
	
	/**
	 * Multiplies the matrix by a scalar.
	 */
	public DoubleMatrix multiply(double scalar) {
		DoubleMatrix result = new DoubleMatrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				result = result.setValue(r, c, getValue(r, c) * scalar);
			}
		}
		return result;
	}
	
	/**
	 * Divides the matrix by a scalar.
	 */
	public DoubleMatrix divide(double scalar) {
		if (scalar == 0) {
			throw new ArithmeticException("Division by 0");
		}
		return multiply(1 / scalar);
	}
	
	/**
	 * Multiplies the matrix by a vector
	 */
	public DoubleVector multiply(DoubleVector x) {
		if (x.getSize() != columns) {
			throw new IllegalArgumentException("Length of vector must be equal to the number" +
					"of columns in the matrix.");
		}
		DoubleVector result = new DoubleVector(rows);
		for (int r = 0; r < rows; r++) {
			double total = 0;
			for (int c = 0; c < columns; c++) {
				total += x.getValue(c) * matrix[r][c];
			}
			result = result.setValue(r, total);
		}
		return result;
	}
	
	/**
	 * Gets a 3x3 matrix which rotates the vector a to point in the direction of vector b
	 */
	public static DoubleMatrix getRotationMatrix(DoubleVector a, DoubleVector b) {
		if (a.getSize() != b.getSize()) {
			throw new IllegalArgumentException("Size of vectors must be equal");
		}
		if (a.getSize() == 2) {
			throw new UnsupportedOperationException("2d rotation matrices not yet implemented");
		}
		if (a.getSize() != 3) {
			throw new IllegalArgumentException("Size of vectors must be 3");
		}
		a = a.normalize();
		b = b.normalize();
		DoubleMatrix result = new DoubleMatrix(3, 3);
		DoubleVector crossRotation = a.crossProduct(b).normalize();
		double dotRotation = a.dotProduct(b);
		double cos = dotRotation;
		double sin = Math.sqrt(1 - cos * cos);
		double x = crossRotation.getValue(0);
		double y = crossRotation.getValue(1);
		double z = crossRotation.getValue(2);
		
		result = result.setValue(0, 0, 1 + (1 - cos) * (x * x - 1));
		result = result.setValue(0, 1, -1 * z * sin + (1 - cos) * x * y);
		result = result.setValue(0, 2, y * sin + (1 - cos) * x * z);
		result = result.setValue(1, 0, z * sin + (1-cos) * x * y);
		result = result.setValue(1, 1, 1 + (1 - cos) * (y * y - 1));
		result = result.setValue(1, 2, -1 * x * sin + (1 - cos) * y * z);
		result = result.setValue(2, 0, -1 * y * sin + (1 - cos) * x * z);
		result = result.setValue(2, 1, x * sin + (1 - cos) * y * z);
		result = result.setValue(2, 2, 1 + (1 - cos) * (z * z - 1));
		return result;
	}
	
	@Override
	public DoubleMatrix clone() {
		DoubleMatrix result = new DoubleMatrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				result.matrix[r][c] = matrix[r][c];
			}
		}
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		return equals((DoubleMatrix) other, EPSILON);
	}
	
	/**
	 * Compares two matrices with the given level of accuracy
	 */
	public boolean equals(DoubleMatrix other, double accuracy) {
		for (int r = 0; r < rows; r++) {
			if (!getRow(r).equals(other.getRow(r))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (int r = 0; r < rows; r++) {
			result += getRow(r) + "\n";
		}
		return result.substring(0, result.length() - 1);
	}
}
