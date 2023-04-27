package io.spring.barcelona.coffee.barista.service;

import io.spring.barcelona.coffee.barista.exceptions.CoffeeNotAvailableException;
import io.spring.barcelona.coffee.barista.orders.Order;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Olga Maciaszek-Sharma
 */
@Component
public class KafkaHandler {

	private static final Log LOG = LogFactory.getLog(KafkaHandler.class);

	private final CoffeeService coffeeService;
	private final KafkaTemplate<Object, Object> template;

	public KafkaHandler(CoffeeService coffeeService, KafkaTemplate<Object, Object> template) {
		this.coffeeService = coffeeService;
		this.template = template;
	}

	@KafkaListener(id = "baristaOrders", topics = "orders")
	public void listen(Order order) {
		LOG.info("Order received: " + order);
		process(order);
	}

	// Visible for testing
	public void process(Order order) {
		Serving serving;
		try {
			serving = coffeeService.prepareServing(order);
		}
		catch (CoffeeNotAvailableException exception) {
			template.send("errors", exception);
			return;
		}
		template.send("servings", serving);
	}
}
