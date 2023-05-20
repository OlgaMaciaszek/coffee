package io.spring.barcelona.coffee.barista;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@SpringBootApplication
public class BaristaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaristaApplication.class, args);
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
	NewTopic servings() {
		return new NewTopic("servings", 1, (short) 1);
	}

	@Bean
	NewTopic errors() {
		return new NewTopic("errors", 1, (short) 1);
	}

	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}

}
