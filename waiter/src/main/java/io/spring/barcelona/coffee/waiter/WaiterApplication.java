package io.spring.barcelona.coffee.waiter;

import io.spring.barcelona.coffee.waiter.orders.Order;
import io.spring.barcelona.coffee.waiter.orders.OrderEntry;
import io.spring.barcelona.coffee.waiter.service.Serving;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WaiterApplication {

	private static final Log LOG = LogFactory.getLog(WaiterApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WaiterApplication.class, args);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> kafkaConsumerFactory,
			KafkaTemplate<Object, Object> template) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		return factory;
	}

	@Bean
	NewTopic orders() {
		return new NewTopic("orders", 1, (short) 1);
	}

	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}

	@Bean
	NewTopic servings() {
		return new NewTopic("servings", 1, (short) 1);
	}

	@Bean
	NewTopic errors() {
		return new NewTopic("errors", 1, (short) 1);
	}

	@KafkaListener(id = "waiterServings", topics = "servings")
	public void listen(Serving serving) {
		LOG.info("Here you are: " + serving);
	}

	@KafkaListener(id = "waiterException", topics = "errors")
	public void errors(Exception exception) {
		LOG.info("We apologise : " + exception.getMessage());
	}

}


