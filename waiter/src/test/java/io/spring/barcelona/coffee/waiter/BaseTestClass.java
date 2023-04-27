package io.spring.barcelona.coffee.waiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import io.spring.barcelona.coffee.waiter.controller.OrderController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * @author Olga Maciaszek-Sharma
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {WaiterApplication.class, BaseTestClass.TestConfig.class})
@AutoConfigureMessageVerifier
@ActiveProfiles("contracts")
@Testcontainers
public class BaseTestClass extends BaseSetup {

	@Container
	static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

	@Autowired
	ApplicationContext context;

	@DynamicPropertySource
	static void kafkaPropertiesBaseClass(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired
	private OrderController orderController;

	public void triggerOrder() {
		orderController.testOrder();
	}

	@Configuration
	@EnableKafka
	static class TestConfig {

		@Bean
		KafkaMessageVerifier kafkaTemplateMessageVerifier() {
			return new KafkaMessageVerifier();
		}

	}

	static class KafkaMessageVerifier implements MessageVerifierReceiver<Message<?>> {

		private static final Log LOG = LogFactory.getLog(KafkaMessageVerifier.class);

		Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();


		@Override
		public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
			broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
			BlockingQueue<Message<?>> messageQueue = broker.get(destination);
			Message<?> message;
			try {
				message = messageQueue.poll(timeout, timeUnit);
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (message != null) {
				LOG.info("Removed a message from a topic [" + destination + "]");
				LOG.info(message.getPayload().toString());
			}
			return message;
		}


		@KafkaListener(id = "waiterOrders", topics = {"orders"})
		public void listen(ConsumerRecord payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
			LOG.info("Got a message from a topic [" + topic + "]");
			Map<String, Object> headers = new HashMap<>();
			new DefaultKafkaHeaderMapper().toHeaders(payload.headers(), headers);
			broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
			BlockingQueue<Message<?>> messageQueue = broker.get(topic);
			messageQueue.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(headers)));
		}

		@Override
		public Message receive(String destination, YamlContract contract) {
			return receive(destination, 15, TimeUnit.SECONDS, contract);
		}

	}
}


