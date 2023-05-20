package io.spring.barcelona.coffee.barista.service;

import io.spring.barcelona.coffee.barista.coffee.Coffee;

import java.util.UUID;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Beverage {

	private UUID uuid;
	private Coffee coffee;

	public static Beverage of(Coffee coffee) {
		return new Beverage(UUID.randomUUID(), coffee);
	}

	Beverage(UUID uuid, Coffee coffee) {
		this.uuid = uuid;
		this.coffee = coffee;
	}

	Beverage() {
	}

	public UUID getUuid() {
		return uuid;
	}

	public Coffee getCoffee() {
		return coffee;
	}

	@Override
	public String toString() {
		return "Beverage{" +
				"uuid=" + uuid +
				", coffee=" + coffee +
				'}';
	}
}
