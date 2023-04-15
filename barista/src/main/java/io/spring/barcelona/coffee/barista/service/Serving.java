package io.spring.barcelona.coffee.barista.service;

import java.util.HashSet;
import java.util.Set;

import io.spring.barcelona.coffee.barista.coffee.Coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Serving {

	private Set<Coffee> coffees = new HashSet<>();

	void addToServing(Coffee coffee) {
		coffees.add(coffee);
	}

	public Set<Coffee> getCoffees() {
		return coffees;
	}

	@Override
	public String toString() {
		return "Serving{" +
				"coffees=" + coffees +
				'}';
	}
}
