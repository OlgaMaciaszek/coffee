package io.spring.barcelona.coffee.barista.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Coffee {

	private String name;

	private int coffeeContent;

	Coffee() {
	}

	public static Coffee espresso() {
		return new Traditional("Espresso", 30);
	}

	public static Coffee cappuccino() {
		return new Traditional("Cappuccino", 60, 60, 60);
	}

	public static Coffee latte() {
		return new Traditional("Latte", 60, 180, 5);
	}

	public static Coffee v60() {
		return new Alternative("V60", 500, BrewingDevice.V60);
	}

	public static Coffee aeroPress() {
		return new Alternative("AeroPress", 200, BrewingDevice.AERO_PRESS);
	}


	Coffee(String name, int coffeeContent) {
		this.name = name;
		this.coffeeContent = coffeeContent;
	}

	public String getName() {
		return name;
	}

	public int getCoffeeContent() {
		return coffeeContent;
	}

	@Override
	public String toString() {
		return "Coffee{" +
				"name='" + name + '\'' +
				", coffeeContent=" + coffeeContent +
				'}';
	}
}
