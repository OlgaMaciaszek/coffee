package io.spring.barcelona.coffee.waiter.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Alternative extends Coffee {

	private BrewingDevice device;

	Alternative() {
	}

	Alternative(String name, int coffeeContent, BrewingDevice device) {
		super(name, coffeeContent);
		this.device = device;
	}

	public BrewingDevice getDevice() {
		return device;
	}

	@Override
	public String toString() {
		return "Alternative Coffee{" +
				"name='" + getName() + '\'' +
				", coffeeContent=" + getCoffeeContent() +
				", device=" + device +
				'}';
	}
}
