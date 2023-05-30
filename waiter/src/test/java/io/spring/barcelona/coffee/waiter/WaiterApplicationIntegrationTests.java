package io.spring.barcelona.coffee.waiter;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {WaiterApplicationIntegrationTests.TestConfig.class, WaiterApplication.class, ContainersConfig.class})
@AutoConfigureStubRunner(ids = "io.spring.barcelona:barista",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@ActiveProfiles("integration")
@ExtendWith(OutputCaptureExtension.class)
class WaiterApplicationIntegrationTests {

    @Autowired
    StubTrigger trigger;

    @Test
    void shouldProcessServing(CapturedOutput output) {
        given(requestSpecification)
                .pathParam("order", "V60")
                .pathParam("count", "60")
                .when().get("/order/{order}/{count}")
                .then().statusCode(201);

        trigger.trigger("serving");

        Awaitility.await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
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

    // RestAssured config
    protected RequestSpecification requestSpecification;

    @LocalServerPort
    protected int localServerPort;

    @BeforeEach
    public void setUpAbstractIntegrationTest() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpecification = new RequestSpecBuilder()
                .setPort(localServerPort)
                .addHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    @Configuration
    static class TestConfig {


        @Bean
        MessageVerifierSender<Message<?>> standaloneMessageVerifier(KafkaProperties properties, KafkaConnectionDetails connectionDetails) {
            return new MessageVerifierSender<>() {

                @Override
                public void send(Message<?> message, String destination, @Nullable YamlContract contract) {
                }

                @Override
                public <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
                    Map<String, Object> newHeaders = headers != null ? new HashMap<>(headers) : new HashMap<>();
                    newHeaders.put(KafkaHeaders.TOPIC, destination);
                    Map<String, Object> map = properties.buildProducerProperties();
                    applyKafkaConnectionDetailsForProducer(map, connectionDetails);
                    DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(map, new StringSerializer(), new StringSerializer());
                    KafkaTemplate<?, ?> kafkaTemplate = new KafkaTemplate<>(factory);
                    kafkaTemplate.send(MessageBuilder.createMessage(payload, new MessageHeaders(newHeaders)));
                }

                private void applyKafkaConnectionDetailsForProducer(Map<String, Object> properties,
                                                                    KafkaConnectionDetails connectionDetails) {
                    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, connectionDetails.getProducerBootstrapServers());
                }
            };
        }

        @Bean
        @Primary
        JsonMessageConverter noopMessageConverter() {
            return new NoopJsonMessageConverter();
        }
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
