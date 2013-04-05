package utilities;

public class DoubleMatrix {
	private int rows;
	private int columns;
	private double[][] matrix;
	
	public DoubleMatrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		matrix = new double[rows][columns];
	}
	
	public double getValue(int row, int column) {
		return matrix[row][column];
	}
	
	public void setValue(int row, int column, double val) {
		matrix[row][column] = val;
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
	
	public DoubleMatrix inverse() {
		if (rows != columns) {
			throw new ArithmeticException("Matrix must be square");
		}
		
		return null;//Not yet implemented
	}
}
