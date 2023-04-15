package io.spring.barcelona.coffee.barista.service;

import java.util.Locale;
import java.util.stream.IntStream;

import io.spring.barcelona.coffee.barista.coffee.Coffees;
import io.spring.barcelona.coffee.barista.orders.Order;

import org.springframework.stereotype.Service;

/**
 * @author Olga Maciaszek-Sharma
 */
@Service
public class CoffeeService {

	public Serving prepareServing(Order order) {
		Serving serving = new Serving();
		order.getEntries().forEach(entry -> IntStream.of(entry.getCount())
				.forEach(i -> serving.addToServing(Coffees.forName(entry.getBeverageName()
						.toLowerCase(Locale.getDefault())))));
		return serving;
	}
}
