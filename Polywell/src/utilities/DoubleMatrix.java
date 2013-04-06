package utilities;

public class DoubleMatrix implements Cloneable{
	public static final double EPSILON = 1.0E-7;
	
	private int rows;
	private int columns;
	private double[][] matrix;
	
	public DoubleMatrix(int rows, int columns) {
		if (rows <= 0 || columns <= 0) {
			throw new IllegalArgumentException("Dimensions must be positive");
		}
		this.rows = rows;
		this.columns = columns;
		matrix = new double[rows][columns];
	}
	
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
	
	public double getValue(int row, int column) {
		return matrix[row][column];
	}
	
	public DoubleMatrix setValue(int row, int column, double val) {
		DoubleMatrix result = clone();
		result.matrix[row][column] = val;
		return result;
	}
	
	public DoubleVector getRow(int row) {
		DoubleVector result = new DoubleVector(columns);
		for (int c = 0; c < columns; c++) {
			result = result.setValue(c, matrix[row][c]);
		}
		return result;
	}
	
	public DoubleVector getColumn(int column) {
		DoubleVector result = new DoubleVector(rows);
		for (int r = 0; r < rows; r++) {
			result = result.setValue(r, matrix[r][column]);
		}
		return result;
	}
	
	public DoubleMatrix setRow(int r, DoubleVector row) {
		DoubleMatrix result = clone();
		
		return result;
	}
	
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
	
	public DoubleMatrix subtract(DoubleMatrix other) {
		return add(other.multiply(-1));
	}
	
	public DoubleMatrix multiply(double scalar) {
		DoubleMatrix result = new DoubleMatrix(rows, columns);
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				result = result.setValue(r, c, getValue(r, c) * scalar);
			}
		}
		return result;
	}
	
	public DoubleMatrix divide(double scalar) {
		if (scalar == 0) {
			throw new ArithmeticException("Division by 0");
		}
		return multiply(1 / scalar);
	}
	
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
