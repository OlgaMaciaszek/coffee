package io.spring.barcelona.coffee.barista.coffee;

/**
 * @author Olga Maciaszek-Sharma
 */
public class Traditional extends Coffee {

	Traditional(String name, int coffeeContent, int steamedMilkContent, int milkFoamContent) {
		super(name, coffeeContent);
		this.steamedMilkContent = steamedMilkContent;
		this.milkFoamContent = milkFoamContent;
	}

	int steamedMilkContent;


	int milkFoamContent;

	public Traditional(String espresso, int coffeeContent) {
		super(espresso, coffeeContent);
	}

	@Override
	public String toString() {
		return "Traditional Coffee{" +
				"name='" + name + '\'' +
				", coffeeContent=" + coffeeContent +
				", steamedMilkContent=" + steamedMilkContent +
				", milkFoamContent=" + milkFoamContent +
				'}';
	}
}
