package test.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import utilities.DoubleVector;

public class DoubleVectorTest {

	private DoubleVector getA() {
		return new DoubleVector(1, 2, 3);
	}
	
	@Test
	public void createEmptyTest() {
		DoubleVector v = new DoubleVector(5);
		for (int i = 0; i < 5; i++) {
			assertNotNull(v.getValue(i));
			assertTrue(0.0 == v.getValue(i));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createNegativeSizeTest() {
		new DoubleVector(-1);
	}
	
	@Test
	public void createFullTest() {
		DoubleVector v = new DoubleVector(1, 2, 3, 4, 5);
		for (int i = 0; i < 5; i++) {
			assertNotNull(v.getValue(i));
			assertTrue(i + 1 == v.getValue(i));
		}
	}
	
	@Test
	public void createListTest() {
		List<Double> vals = Lists.<Double>newArrayList();
		DoubleVector v = new DoubleVector(vals);
		assertEquals(vals, v.getValues());
	}
	
	@Test
	public void getSizeTest() {
		DoubleVector v = new DoubleVector(5);
		assertTrue(5 == v.getSize());
	}
	
	@Test
	public void setValueTest() {
		DoubleVector v = getA();
		v = v.setValue(0, 5.6);
		assertFalse(1 == v.getValue(0));
		assertTrue(5.6 == v.getValue(0));
	}
	
	@Test
	public void setValueImmutableTest() {
		DoubleVector v = getA();
		DoubleVector alias = v;
		v = v.setValue(0,  5.6);
		assertTrue(1 == alias.getValue(0));
		assertTrue(5.6 == v.getValue(0));
	}
	
	@Test
	public void equalsTest() {
		DoubleVector a = getA();
		DoubleVector b = getA();
		assertEquals(a, b);
	}
	
	@Test
	public void addTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(0, 1, 2);
		DoubleVector sum = a.add(b);
		DoubleVector expectedResult = new DoubleVector(1, 3, 5);
		assertNotEquals(sum, a);
		assertNotEquals(sum, b);
		assertEquals(expectedResult, sum);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addSizeMismatchTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(1, 2);
		a.add(b);
	}
	
	@Test
	public void subtractTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(0, 1, 2);
		DoubleVector difference = a.subtract(b);
		DoubleVector expectedResult = new DoubleVector(1, 1, 1);
		assertNotEquals(difference, a);
		assertNotEquals(difference, b);
		assertEquals(expectedResult, difference);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void subtractSizeMismatchTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(1, 2);
		a.subtract(b);
	}
	
	@Test
	public void multiplyTest() {
		DoubleVector a = getA();
		double b = 2;
		DoubleVector product = a.multiply(b);
		DoubleVector expectedResult = new DoubleVector(2, 4, 6);
		assertNotEquals(product, a);
		assertEquals(expectedResult, product);
	}
	
	@Test
	public void divideTest() {
		DoubleVector a = getA();
		double b = 2;
		DoubleVector dividend = a.divide(b);
		DoubleVector expectedResult = new DoubleVector(0.5, 1, 1.5);
		assertNotEquals(dividend, a);
		assertEquals(expectedResult, dividend);
	}
	
	@Test(expected = ArithmeticException.class)
	public void divideBy0Test() {
		DoubleVector a = getA();
		double b = 0;
		a.divide(b);
	}
	
	@Test
	public void dotProductTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(3, 2, 4);
		double result = a.dotProduct(b);
		double expectedResult = 19;
		assertEquals(expectedResult, result, DoubleVector.EPSILON);
		assertEquals(getA(), a);
		assertEquals(new DoubleVector(3, 2, 4), b);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void dotProductSizeMismatchTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(1, 2);
		a.dotProduct(b);
	}
	
	@Test
	public void crossProductTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(3, 2, 4);
		DoubleVector cross = a.crossProduct(b);
		DoubleVector expectedResult = new DoubleVector(2, 5, -4);
		assertEquals(expectedResult, cross);
		assertEquals(getA(), a);
		assertEquals(new DoubleVector(3, 2, 4), b);
		DoubleVector crossReverse = b.crossProduct(a);
		assertEquals(expectedResult.multiply(-1), crossReverse);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void crossSizeMismatchTest() {
		DoubleVector a = getA();
		DoubleVector b = new DoubleVector(1, 2);
		a.crossProduct(b);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void crossSizeNot3Test() {
		DoubleVector a = new DoubleVector(1, 2);
		DoubleVector b = new DoubleVector(1, 2);
		a.crossProduct(b);
	}
	
	@Test
	public void normTest() {
		DoubleVector a = getA();
		double norm = a.norm();
		double expectedResult = Math.sqrt(14);
		assertEquals(expectedResult, norm, DoubleVector.EPSILON);
	}
	
	@Test
	public void normalizeTest() {
		DoubleVector a = getA();
		DoubleVector normalized = a.normalize();
		DoubleVector expectedResult = a.divide(Math.sqrt(14));
		assertEquals(expectedResult, normalized);
		assertNotEquals(normalized, a);
	}
	
	@Test
	public void absTest() {
		DoubleVector a = new DoubleVector(-1, 2, -3);
		DoubleVector abs = a.abs();
		DoubleVector expectedResult = getA();
		assertEquals(expectedResult, abs);
		assertNotEquals(abs, a);
	}
	
	@Test
	public void minTest() {
		DoubleVector a = getA();
		double min = a.min();
		double expectedResult = 1;
		assertEquals(expectedResult, min, DoubleVector.EPSILON);
	}
	
	@Test
	public void maxTest() {
		DoubleVector a = getA();
		double max = a.max();
		double expectedResult = 3;
		assertEquals(expectedResult, max, DoubleVector.EPSILON);
	}
	
	@Test
	public void cloneTest() {
		DoubleVector a = getA();
		DoubleVector copy = a.clone();
		assertEquals(a, copy);
		a = a.setValue(1, 8.0);
		assertNotEquals(a, copy);
	}
}
