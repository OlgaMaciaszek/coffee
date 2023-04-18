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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestConfig.class, WaiterApplication.class})
@AutoConfigureStubRunner(ids = "io.spring.barcelona:barista", stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class WaiterApplicationIntegrationTests {


	@Container
	static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired
	StubTrigger trigger;

	@Test
	void shouldProcessServing(CapturedOutput output) {
		trigger.trigger("serving");

		Awaitility.await().atMost(Duration.ofSeconds(30))
				.pollInterval(Duration.ofSeconds(5))
				.untilAsserted(() -> {
					assertThat(output).contains("\\\"coffee\\\":{\\\"name\\\":\\\"V60\\\",\\\"coffeeContent\\\":\\\"500\\\",\\\"device\\\":\\\"V60\\");
					assertThat(output).contains("\"coffee\\\":{\\\"name\\\":\\\"Latte\\\",\\\"coffeeContent\\\":\\\"60\\\",\\\"steamedMilkContent\\\":\\\"180\\\",\\\"milkFoamContent\\\":\\\"5\\\"");
				});
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
}
