package io.spring.barcelona.coffee.barista.service;

import io.spring.barcelona.coffee.barista.coffee.Coffees;
import io.spring.barcelona.coffee.barista.orders.Order;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.stream.IntStream;

/**
 * @author Olga Maciaszek-Sharma
 */
@Service
public class CoffeeService {

	public Serving prepareServing(Order order) {
		Serving serving = new Serving();
		order.getEntries().forEach(entry ->
				IntStream.range(0, entry.getCount())
						.forEach(i ->
								serving.add(Beverage.of(Coffees.forName(entry.getBeverageName()
										.toLowerCase(Locale.getDefault()))))));
		return serving;
	}
}
