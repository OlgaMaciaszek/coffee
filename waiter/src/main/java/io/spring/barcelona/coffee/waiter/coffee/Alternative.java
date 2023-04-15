package io.spring.barcelona.coffee.waiter.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Alternative extends Coffee {

	Alternative(String name, int coffeeContent, BrewingDevice device) {
		super(name, coffeeContent);
		this.device = device;
	}

	BrewingDevice device;

	@Override
	public String toString() {
		return "Alternative Coffee{" +
				"name='" + name + '\'' +
				", coffeeContent=" + coffeeContent +
				", device=" + device +
				'}';
	}
}
