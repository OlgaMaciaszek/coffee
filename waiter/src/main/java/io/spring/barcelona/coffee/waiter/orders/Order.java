package io.spring.barcelona.coffee.waiter.orders;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Order {

	private Set<OrderEntry> entries = new HashSet<>();

	public void add(OrderEntry orderEntry) {
		entries.add(orderEntry);
	}

	public Set<OrderEntry> getEntries() {
		return entries;
	}

	@Override
	public String toString() {
		return "Order{" +
				"entries=" + entries +
				'}';
	}
}
