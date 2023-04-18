package io.spring.barcelona.coffee.barista.service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Serving {

	private final Set<Beverage> beverages = new HashSet<>();

	Serving() {
	}

	public void add(Beverage beverage) {
		beverages.add(beverage);
	}

	public Set<Beverage> getBeverages() {
		return beverages;
	}

	@Override
	public String toString() {
		return "Serving{" +
				"beverages=" + beverages +
				'}';
	}
}
