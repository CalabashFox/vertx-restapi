package io.mu.restapi;

import java.util.function.Predicate;

public class Utils {

	public static <T> Predicate<T> not(Predicate<T> predicate) {
		return predicate.negate();
	}

}
