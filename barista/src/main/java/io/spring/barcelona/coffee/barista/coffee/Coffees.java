package io.spring.barcelona.coffee.barista.coffee;

import java.util.Map;

import io.spring.barcelona.coffee.barista.exceptions.CoffeeNotAvailableException;

import static io.spring.barcelona.coffee.barista.coffee.Coffee.aeroPress;
import static io.spring.barcelona.coffee.barista.coffee.Coffee.cappuccino;
import static io.spring.barcelona.coffee.barista.coffee.Coffee.espresso;
import static io.spring.barcelona.coffee.barista.coffee.Coffee.latte;
import static io.spring.barcelona.coffee.barista.coffee.Coffee.v60;

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
