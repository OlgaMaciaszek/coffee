package io.spring.barcelona.coffee.barista.coffee;

import io.spring.barcelona.coffee.barista.exceptions.CoffeeNotAvailableException;

import java.util.Map;

import static io.spring.barcelona.coffee.barista.coffee.Coffee.*;

/**
 * @author Olga Maciaszek-Sharma
 */
public final class Coffees {

	private Coffees() {
		throw new IllegalStateException("Do not instantiate utility class");
	}

	private static final Map<String, Coffee> coffees = Map.of("espresso", espresso(),
			"cappuccino", cappuccino(),
			"latte", latte(),
			"v60", v60(),
			"aeropress", aeroPress());

	public static Coffee forName(String name) {
		if (!coffees.containsKey(name)) {
			throw new CoffeeNotAvailableException(name);
		}
		return coffees.get(name);
	}
}
