package io.spring.barcelona.coffee.barista.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Traditional extends Coffee {


	private int steamedMilkContent;
	private int milkFoamContent;

	Traditional() {
	}

	Traditional(String name, int coffeeContent, int steamedMilkContent, int milkFoamContent) {
		super(name, coffeeContent);
		this.steamedMilkContent = steamedMilkContent;
		this.milkFoamContent = milkFoamContent;
	}

	Traditional(String espresso, int coffeeContent) {
		super(espresso, coffeeContent);
	}

	public int getSteamedMilkContent() {
		return steamedMilkContent;
	}

	public int getMilkFoamContent() {
		return milkFoamContent;
	}

	@Override
	public String toString() {
		return "Traditional Coffee{" +
				"name='" + getName() + '\'' +
				", coffeeContent=" + getCoffeeContent() +
				", steamedMilkContent=" + steamedMilkContent +
				", milkFoamContent=" + milkFoamContent +
				'}';
	}
}
