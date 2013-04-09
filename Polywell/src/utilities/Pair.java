package utilities;

import com.google.common.base.Objects;

/**
 * Utility class for a pair of two objects
 * @author Ryan Dewey
 */
public class Pair<A, B> {
	private A a;
	private B b;
	
	/**
	 * Cannot be publicly instantiated
	 */
	private Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * Creates a new pair with the given objects
	 */
	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}
	
	public A getA() {
		return a;
	}
	
	public B getB() {
		return b;
	}
	
	@Override
	public String toString() {
		return a + ", " + b;
	}
	
	@Override
	public boolean equals(Object other) {
		return Objects.equal(this, other);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}
}