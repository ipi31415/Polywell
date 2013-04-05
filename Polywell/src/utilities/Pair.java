package utilities;

import com.google.common.base.Objects;

public class Pair<A, B> {
	public A a;
	public B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
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