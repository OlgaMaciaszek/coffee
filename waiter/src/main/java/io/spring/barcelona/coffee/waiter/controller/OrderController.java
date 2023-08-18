package io.spring.barcelona.coffee.waiter.controller;

import io.spring.barcelona.coffee.waiter.orders.Order;
import io.spring.barcelona.coffee.waiter.orders.OrderEntry;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Olga Maciaszek-Sharma
 */
@RestController
public class OrderController {

	private static final Log LOG = LogFactory.getLog(OrderController.class);

	@Autowired
	private  KafkaTemplate<Object, Object> template;

	@GetMapping("/order/{name}/{count}")
	public ResponseEntity<Void> order(@PathVariable("name") String beverageName, @PathVariable int count) {
		Order order = new Order();
		order.add(new OrderEntry(beverageName, count));
		LOG.info("Sending order: " + order);
		template.send("orders", order);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/order")
	public ResponseEntity<Void> testOrder() {
		Order order = new Order();
		order.add(new OrderEntry("latte", 6));
		order.add(new OrderEntry("v60", 8));
		LOG.info("Sending order: " + order);
		template.send("orders", order);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}
