package com.github.lukelinkwalker.orchestrator.Util;

public class TupleFactory<T1, T2> {
	public Tuple<T1, T2> create(T1 a, T2 b) {
		return new Tuple<>(a, b);
	}
}
