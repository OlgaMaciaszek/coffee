package io.spring.barcelona.coffee.waiter;

import io.spring.barcelona.coffee.waiter.orders.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WaiterApplication {

	@Autowired
	private KafkaTemplate<Object, Object> template;

	public static void main(String[] args) {
		SpringApplication.run(WaiterApplication.class, args);
	}

	@PostMapping("/order/{name}/{count}")
	ResponseEntity<Void> order(@PathVariable("name") String beverageName, @PathVariable int count) {
		template.send("orders", new Order(beverageName, count));
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}


