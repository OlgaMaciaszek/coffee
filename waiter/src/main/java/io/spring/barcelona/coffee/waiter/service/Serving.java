package io.spring.barcelona.coffee.waiter.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.spring.barcelona.coffee.waiter.coffee.Coffee;

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
