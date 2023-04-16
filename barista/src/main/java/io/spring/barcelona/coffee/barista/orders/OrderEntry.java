package io.spring.barcelona.coffee.barista.orders;

/**
 * @author Olga Maciaszek-Sharma
 */
public class OrderEntry {

	private String beverageName;
	private int count;

	OrderEntry() {
	}

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

	@Override
	public String toString() {
		return "OrderEntry{" +
				"beverageName='" + beverageName + '\'' +
				", count=" + count +
				'}';
	}
}
