package io.spring.barcelona.coffee.waiter.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Coffee {

	Coffee(String name, int coffeeContent) {
		this.name = name;
		this.coffeeContent = coffeeContent;
	}

	String name;

	int coffeeContent;

	@Override
	public String toString() {
		return "Coffee{" +
				"name='" + name + '\'' +
				", coffeeContent=" + coffeeContent +
				'}';
	}
}
