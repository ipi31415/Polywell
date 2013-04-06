package test.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import utilities.DoubleMatrix;
import utilities.DoubleVector;

public class DoubleMatrixTest {
	
	private DoubleMatrix getA() {
		return new DoubleMatrix(new DoubleVector(1, 2, 3, 4),
				new DoubleVector(4, 5, 6, 7),
				new DoubleVector(7, 8, 9, 10));
	}
	
	private DoubleMatrix getSmallA() {
		return new DoubleMatrix(new DoubleVector(1, 2, 3),
				new DoubleVector(4, 5, 6),
				new DoubleVector(7, 8, 9));
	}
	
	private DoubleMatrix getB() {
		return new DoubleMatrix(new DoubleVector(0, 2, 3, 4),
				new DoubleVector(4, 5, 6, 7),
				new DoubleVector(7, 8, 9, 10));
	}
	
	@Test
	public void createEmptyTest() {
		DoubleMatrix a = new DoubleMatrix(5, 7);
		for (int r = 0; r < 5; r++) {
			for (int c = 0; c < 5; c++) {
				assertEquals(0.0, a.getValue(r, c), DoubleMatrix.EPSILON);
			}
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeSizeTest() {
		new DoubleMatrix(5, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeSizeTest2() {
		new DoubleMatrix(-1, 5);
	}
	
	@Test
	public void createFullTest() {
		DoubleMatrix a = getA();
		assertEquals(new DoubleVector(1, 2, 3, 4), a.getRow(0));
		assertEquals(new DoubleVector(4, 5, 6, 7), a.getRow(1));
		assertEquals(new DoubleVector(7, 8, 9, 10), a.getRow(2));
	}
	
	@Test
	public void getRowTest() {
		DoubleMatrix a = getA();
		DoubleVector row2 = a.getRow(1);
		DoubleVector expectedResult = new DoubleVector(4, 5, 6, 7);
		assertEquals(expectedResult, row2);
	}
	
	@Test
	public void getColumnTest() {
		DoubleMatrix a = getA();
		DoubleVector column2 = a.getColumn(1);
		DoubleVector expectedResult = new DoubleVector(2, 5, 8);
		assertEquals(expectedResult, column2);
	}
	
	@Test
	public void setValueTest() {
		DoubleMatrix a = getA();
		DoubleMatrix changed = a.setValue(1, 2, 3);
		assertEquals(3, changed.getValue(1, 2), DoubleMatrix.EPSILON);
		assertEquals(6, a.getValue(1,  2), DoubleMatrix.EPSILON);
	}
	
	@Test
	public void addTest() {
		DoubleMatrix a = getA();
		DoubleMatrix b = getB();
		DoubleMatrix sum = a.add(b);
		DoubleMatrix expectedResult = new DoubleMatrix(new DoubleVector(1, 4, 6, 8), 
				new DoubleVector(8, 10, 12, 14), new DoubleVector(14, 16, 18, 20));
		assertNotEquals(sum, a);
		assertNotEquals(sum, b);
		assertEquals(expectedResult, sum);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addSizeMismatchTest() {
		DoubleMatrix a = getA();
		DoubleMatrix b = getSmallA();
		a.add(b);
	}
	
	@Test
	public void subtractTest() {
		DoubleMatrix a = getA();
		DoubleMatrix b = getB();
		DoubleMatrix difference = a.subtract(b);
		DoubleMatrix expectedResult = new DoubleMatrix(new DoubleVector(1, 0, 0, 0), 
				new DoubleVector(0, 0, 0, 0), new DoubleVector(0, 0, 0, 0));
		assertNotEquals(difference, a);
		assertNotEquals(difference, b);
		assertEquals(expectedResult, difference);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void subtractSizeMismatchTest() {
		DoubleMatrix a = getA();
		DoubleMatrix b = getSmallA();
		a.subtract(b);
	}
	
	@Test
	public void multiplyScalarTest() {
		DoubleMatrix a = getA();
		double b = 2;
		DoubleMatrix product = a.multiply(b);
		DoubleMatrix expectedResult = new DoubleMatrix(new DoubleVector(2, 4, 6, 8), 
				new DoubleVector(8, 10, 12, 14), new DoubleVector(14, 16, 18, 20));
		assertNotEquals(product, a);
		assertEquals(expectedResult, product);
	}
	
	@Test
	public void divideTest() {
		DoubleMatrix a = getA();
		double b = 2;
		DoubleMatrix dividend = a.divide(b);
		DoubleMatrix expectedResult = new DoubleMatrix(new DoubleVector(.5, 1, 1.5, 2), 
				new DoubleVector(2, 2.5, 3, 3.5), new DoubleVector(3.5, 4, 4.5, 5));
		assertNotEquals(dividend, a);
		assertEquals(expectedResult, dividend);
	}
	
	@Test(expected = ArithmeticException.class)
	public void divideBy0Test() {
		DoubleMatrix a = getA();
		double b = 0;
		a.divide(b);
	}
	
	@Test
	public void multiplyVectorTest() {
		DoubleMatrix a = getA();
		DoubleVector b = new DoubleVector(1, 2, 3, 4);
		DoubleVector product = a.multiply(b);
		DoubleVector expectedResult = new DoubleVector(30, 60, 90);
		assertEquals(expectedResult, product);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void multiplyVectorSizeMismatchTest() {
		DoubleMatrix a = getA();
		DoubleVector b = new DoubleVector(1, 2, 3);
		a.multiply(b);
	}
}
