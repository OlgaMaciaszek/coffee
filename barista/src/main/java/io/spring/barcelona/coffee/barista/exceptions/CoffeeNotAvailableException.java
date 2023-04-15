package io.spring.barcelona.coffee.barista.exceptions;

/**
 * @author Olga Maciaszek-Sharma
 */
public class CoffeeNotAvailableException extends RuntimeException {

	public CoffeeNotAvailableException(String coffeeName) {
		super("We currently do not have the following coffee in our menu: " + coffeeName);
	}
}
