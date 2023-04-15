package io.spring.barcelona.coffee.waiter.orders;

/**
 * @author Olga Maciaszek-Sharma
 */
public class OrderEntry {

	private final String beverageName;
	private final int count;

	public OrderEntry(String beverageName, int count) {
		this.beverageName = beverageName;
		this.count = count;
	}

	public String getBeverageName() {
		return beverageName;
	}

	public int getCount() {
		return count;
	}
}
