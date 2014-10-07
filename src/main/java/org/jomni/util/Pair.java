/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.jomni.util;

/**
 * Immutable topple pair
 */
public class Pair<A, B> {

	private A a;
	private B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public static <T,K> Pair<T,K> newPair(T a, K b) {
		return new Pair(a, b);
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public String toString() {
		return new StringBuilder("(").append(a).append('=').append(b).append(')').toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Pair)) return false;

		Pair pair = (Pair) o;

		if (a != null ? !a.equals(pair.a) : pair.a != null) return false;
		if (b != null ? !b.equals(pair.b) : pair.b != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = a != null ? a.hashCode() : 0;
		result = 31 * result + (b != null ? b.hashCode() : 0);
		return result;
	}


}
