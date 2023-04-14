package io.spring.barcelona.coffee.waiter.orders;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Order {

	private String beverageName;

	private int count;

	public Order(String beverageName, int count) {
		this.beverageName = beverageName;
		this.count = count;
	}

	public String getBeverageName() {
		return beverageName;
	}

	public void setBeverageName(String beverageName) {
		this.beverageName = beverageName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
