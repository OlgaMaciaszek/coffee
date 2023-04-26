package io.spring.barcelona.coffee.waiter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestConfig.class, WaiterApplication.class})
@AutoConfigureStubRunner(ids = "io.spring.barcelona:barista", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("integration")
@ExtendWith(OutputCaptureExtension.class)
@Testcontainers
class WaiterApplicationIntegrationTests {


	@Container
	static KafkaContainer kafkaForContracts = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafkaForContracts::getBootstrapServers);
	}

	@Autowired
	StubTrigger trigger;

	@Test
	void shouldProcessServing(CapturedOutput output) {
		trigger.trigger("serving");

		Awaitility.await().atMost(Duration.ofSeconds(30))
				.pollInterval(Duration.ofSeconds(5))
				.untilAsserted(() -> assertThat(output).contains("""
						Here you are: Serving{beverages=[Beverage{""", """
						coffee=Coffee{name='Latte', coffeeContent=60}}""", """
						coffee=Coffee{name='V60', coffeeContent=500}}"""));
	}

	@Test
	void shouldProcessError(CapturedOutput output) {
		trigger.trigger("error");

		Awaitility.await().atMost(Duration.ofSeconds(30))
				.pollInterval(Duration.ofSeconds(5))
				.untilAsserted(() -> assertThat(output).contains("We currently do not have the following coffee in our menu: expresso"));
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
