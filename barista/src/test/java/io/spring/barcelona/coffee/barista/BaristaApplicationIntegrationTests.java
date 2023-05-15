package io.spring.barcelona.coffee.barista;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Olga Maciaszek-Sharma
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestConfig.class, BaristaApplication.class})
@AutoConfigureStubRunner(ids = "io.spring.barcelona:waiter", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("integration")
@ExtendWith(OutputCaptureExtension.class)
@Testcontainers
class BaristaApplicationIntegrationTests {

	private static final Log LOG = LogFactory.getLog(BaristaApplicationIntegrationTests.class);

	BlockingQueue<Message<?>> broker = new ArrayBlockingQueue<>(1);

	@Container
	@ServiceConnection
	static KafkaContainer kafkaForContracts = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

	@Autowired
	StubTrigger trigger;

	@Test
	void shouldProcessOrder() throws InterruptedException {
		trigger.trigger("order");

		Message<?> servingMessage = broker.poll(10, TimeUnit.SECONDS);

		assertThat(servingMessage).isNotNull();
		assertThat(servingMessage.getPayload().toString()).contains("""
				"coffee":{"name":"Latte","coffeeContent":60,"steamedMilkContent":180,"milkFoamContent":5""");
	}


	@KafkaListener(id = "baristaServings", topics = "servings")
	public void listen(ConsumerRecord payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		LOG.info("Got a message from a topic [" + topic + "]");
		Map<String, Object> headers = new HashMap<>();
		new DefaultKafkaHeaderMapper().toHeaders(payload.headers(), headers);
		broker.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(headers)));
	}

}

@Configuration
class TestConfig {

	@Bean
	MessageVerifierSender<Message<?>> standaloneMessageVerifier(KafkaTemplate kafkaTemplate) {
		return new MessageVerifierSender<>() {

			@Override
			public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
			}

			@Override
			public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
				Map<String, Object> newHeaders = headers != null ? new HashMap<>(headers) : new HashMap<>();
				newHeaders.put(KafkaHeaders.TOPIC, destination);
				kafkaTemplate.send(MessageBuilder.createMessage(payload, new MessageHeaders(newHeaders)));
			}
		};
	}

	@Bean
	@Primary
	JsonMessageConverter noopMessageConverter() {
		return new NoopJsonMessageConverter();
	}
}

class NoopJsonMessageConverter extends JsonMessageConverter {

	NoopJsonMessageConverter() {
	}

	@Override
	protected Object convertPayload(Message<?> message) {
		return message.getPayload();
	}
}
