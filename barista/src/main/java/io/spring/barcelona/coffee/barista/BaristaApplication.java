package io.spring.barcelona.coffee.barista;

import io.spring.barcelona.coffee.barista.orders.Order;
import io.spring.barcelona.coffee.barista.service.CoffeeService;
import io.spring.barcelona.coffee.barista.service.Serving;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@SpringBootApplication
public class BaristaApplication {

	private static final Log LOG = LogFactory.getLog(BaristaApplication.class);

	@Autowired
	private KafkaTemplate<Object, Object> template;

	@Autowired
	private CoffeeService coffeeService;

	public static void main(String[] args) {
		SpringApplication.run(BaristaApplication.class, args);
	}

	@Bean
	NewTopic orders() {
		return new NewTopic("orders", 1, (short) 1);
	}

	@Bean
	NewTopic servings() {
		return new NewTopic("servings", 1, (short) 1);
	}

	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}

	@KafkaListener(id = "barista", topics = "orders")
	public void listen(Order order) {
		LOG.info(order);
		Serving serving = coffeeService.prepareServing(order);
		template.send("servings", serving);
	}

}
